package com.qubacy.geoqq.data.image.repository.source.local

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.image._common.extension.ImageExtension
import com.qubacy.geoqq.data.image._common.util.resolver.extension.getFileExtensionByUri
import com.qubacy.geoqq.data.image._common.util.resolver.extension.getImageBitmapByUri
import com.qubacy.geoqq.data.image.repository.source.local.entity.ImageEntity
import com.qubacy.geoqq.data.image.repository._common.RawImage

class LocalImageDataSource(
    private val mContentResolver: ContentResolver
) : DataSource {
    companion object {
        const val TAG = "LocalImageDataSource"

        const val DEFAULT_IMAGE_QUALITY = 100

        val IMAGE_COLUMNS_TO_LOAD = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.TITLE
        )

        const val IMAGE_PREFIX = "image"
    }

    fun loadImage(imageId: Long): ImageEntity? {
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

    fun loadImages(imageIds: List<Long>): List<ImageEntity>? {
        val imageUris = mutableListOf<ImageEntity>()

        for (imageId in imageIds) {
            val imageEntity = loadImage(imageId) ?: return null

            imageUris.add(imageEntity)
        }

        return imageUris
    }

    fun saveImage(rawImage: RawImage): ImageEntity? {
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

    fun saveImages(rawImages: List<RawImage>): List<ImageEntity>? {
        val imageEntities = mutableListOf<ImageEntity>()

        for (rawImage in rawImages) {
            val imageEntity = saveImage(rawImage) ?: return null

            imageEntities.add(imageEntity)
        }

        return imageEntities
    }

    fun getImageDataByUri(imageUri: Uri): RawImage? {
        val extensionString = mContentResolver.getFileExtensionByUri(imageUri)
        val bitmap = mContentResolver.getImageBitmapByUri(imageUri)

        if (bitmap == null) return null

        val extension = ImageExtension.getFormatByString(extensionString)

        return RawImage(extension = extension, content = bitmap)
    }

//    fun getImageIdByUri(imageUri: Uri): Long? {
//        contentResolver.query(
//            imageUri, IMAGE_COLUMNS_TO_LOAD,
//            null, null, null
//        )?.use {
//            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE)
//
//            if (!it.moveToNext()) return null
//
//            val imageResourceTitle = it.getString(titleColumn)
//            val imageIdString = imageResourceTitle.substring(IMAGE_PREFIX.length)
//
//            return imageIdString.toLong()
//        }
//
//        return null
//    }
//
//    fun getImageIdsByUris(imageUris: List<Uri>): List<Long>? {
//        val imageIds = mutableListOf<Long>()
//
//        for (imageUri in imageUris) {
//            val imageId = getImageIdByUri(imageUri) ?: return null
//
//            imageIds.add(imageId)
//        }
//
//        return imageIds
//    }

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