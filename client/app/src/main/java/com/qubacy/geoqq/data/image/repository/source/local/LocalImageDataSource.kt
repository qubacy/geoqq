package com.qubacy.geoqq.data.image.repository.source.local

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore.Images
import android.util.Log
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import java.io.InputStream
import java.io.OutputStream


class LocalImageDataSource(
    val contentResolver: ContentResolver
) : DataSource {
    companion object {
        const val TAG = "LocalImageDataSource"

        const val DEFAULT_IMAGE_MIME_TYPE = "image/jpeg"
        const val DEFAULT_IMAGE_QUALITY = 50

        val IMAGE_COLUMNS_TO_LOAD = arrayOf(
            Images.ImageColumns._ID,
            Images.ImageColumns.TITLE
        )
    }

    fun loadImage(title: String): Uri? {
        val selection = "${Images.ImageColumns.TITLE} == ?"
        val selectionArgs = arrayOf(title)

        val cursor = contentResolver.query(
            Images.Media.EXTERNAL_CONTENT_URI,
            IMAGE_COLUMNS_TO_LOAD,
            selection,
            selectionArgs,
            null
        )

        if (cursor == null) return null

        val idColumn = cursor.getColumnIndexOrThrow(Images.ImageColumns._ID)

        if (!cursor.moveToNext()) return null

        val imageResourceId = cursor.getLong(idColumn)
        val imageUri = ContentUris.withAppendedId(
            Images.Media.EXTERNAL_CONTENT_URI,
            imageResourceId
        )

        return imageUri
    }

    fun saveImageOnDevice(title: String, image: Bitmap): Uri {
        val contentValues = ContentValues()

        contentValues.put(Images.Media.TITLE, title)
        contentValues.put(Images.Media.DISPLAY_NAME, title)
        contentValues.put(Images.Media.DESCRIPTION, String())
        contentValues.put(Images.Media.MIME_TYPE, DEFAULT_IMAGE_MIME_TYPE)

        val curDate = System.currentTimeMillis()

        contentValues.put(Images.Media.DATE_ADDED, curDate)
        contentValues.put(Images.Media.DATE_TAKEN, curDate)

        try {
            val contentUri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (contentUri == null) throw IllegalStateException()

            val imageOutputStream = contentResolver.openOutputStream(contentUri)

            if (imageOutputStream == null) throw IllegalStateException()

            try {
                image.compress(Bitmap.CompressFormat.JPEG, DEFAULT_IMAGE_QUALITY, imageOutputStream)

            } finally {
                imageOutputStream.close()
            }

            return contentUri

        } catch (e: Exception) {
            Log.d(TAG, e.message ?: "Unknown exception!")

            throw e // todo: think of this.. do we really need to act like this??
        }
    }

    fun getImageBitmapByUri(imageUri: Uri): Bitmap? {
        var imageStream: InputStream? = null
        var imageBitmap: Bitmap? = null

        try {
            imageStream = contentResolver.openInputStream(imageUri)
            imageBitmap = BitmapFactory.decodeStream(imageStream)

        } catch (e: Exception) {
            Log.d(TAG, "Exception: ${e.message}")

        } finally {
            imageStream?.close()
        }

        return imageBitmap
    }
}