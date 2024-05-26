package com.qubacy.geoqq.data.image.repository._common.source.local.content.impl

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.qubacy.geoqq.data.image._common.extension.ImageExtension
import com.qubacy.geoqq.data.image._common.util.resolver.extension.getFileExtensionByUri
import com.qubacy.geoqq.data.image._common.util.resolver.extension.getImageBitmapByUri
import com.qubacy.geoqq.data.image.repository._common.source.local.content._common.entity.ImageEntity
import com.qubacy.geoqq.data.image.repository._common.RawImage
import com.qubacy.geoqq.data.image.repository._common.source.local.content._common.LocalImageContentStoreDataSource
import javax.inject.Inject

class LocalImageContentStoreDataSourceImpl @Inject constructor(
    private val mContentResolver: ContentResolver
) : LocalImageContentStoreDataSource {
    companion object {
        const val TAG = "LocalImageDataSource"

        const val DEFAULT_IMAGE_QUALITY = 100

        val IMAGE_COLUMNS_TO_LOAD = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.TITLE
        )

        const val IMAGE_PREFIX = "image"
    }

    override fun loadImage(imageId: Long): ImageEntity? {
        val title = getImageTitleFromImageId(imageId)

        val selection = "${MediaStore.Images.ImageColumns.TITLE} == ?"
        val selectionArgs = arrayOf(title)

        mContentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_COLUMNS_TO_LOAD,
            selection, selectionArgs, null
        )?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)

            if (!it.moveToNext()) return null

            val imageResourceId = it.getLong(idColumn)
            val imageUri = ContentUris
                .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageResourceId)

            return ImageEntity(imageId, imageUri)
        }

        return null
    }

    override fun saveImage(rawImage: RawImage): ImageEntity? {
        val title = getImageTitleFromImageId(rawImage.id!!)
        val extension = ImageExtension.getStringByFormat(rawImage.extension)
        val mimeType = getMimeTypeFromExtension(extension)

        val imageContentValues = createImageContentValues(title, mimeType)

        try {
            val contentUri = mContentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageContentValues
            ) ?: return null

            mContentResolver.openOutputStream(contentUri)?.use {
                rawImage.content.compress(rawImage.extension, DEFAULT_IMAGE_QUALITY, it)
            }

            return ImageEntity(rawImage.id, contentUri)

        }
        catch (e: Exception) { e.printStackTrace() }

        return null
    }

    override fun getImageDataByUri(imageUri: Uri): RawImage? {
        val extensionString = mContentResolver.getFileExtensionByUri(imageUri)
        val bitmap = mContentResolver.getImageBitmapByUri(imageUri)

        if (bitmap == null) return null

        val extension = ImageExtension.getFormatByString(extensionString)

        return RawImage(extension = extension, content = bitmap)
    }

    private fun getImageTitleFromImageId(imageId: Long): String {
        return (IMAGE_PREFIX + imageId.toString())
    }

    private fun getMimeTypeFromExtension(extension: String): String {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
    }

    private fun createImageContentValues(title: String, mimeType: String): ContentValues {
        val imageContentValues = ContentValues()

        imageContentValues.put(MediaStore.Images.Media.TITLE, title)
        imageContentValues.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        imageContentValues.put(MediaStore.Images.Media.DESCRIPTION, String())
        imageContentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        val curDate = System.currentTimeMillis()

        imageContentValues.put(MediaStore.Images.Media.DATE_ADDED, curDate)
        imageContentValues.put(MediaStore.Images.Media.DATE_TAKEN, curDate)

        return imageContentValues
    }
}