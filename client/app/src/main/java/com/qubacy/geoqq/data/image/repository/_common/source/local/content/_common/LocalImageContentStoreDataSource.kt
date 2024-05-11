package com.qubacy.geoqq.data.image.repository._common.source.local.content._common

import android.net.Uri
import com.qubacy.geoqq.data.image.repository._common.RawImage
import com.qubacy.geoqq.data.image.repository._common.source.local.content._common.entity.ImageEntity

interface LocalImageContentStoreDataSource {
    fun loadImage(imageId: Long): ImageEntity?
    fun loadImages(imageIds: List<Long>): List<ImageEntity>? {
        val imageUris = mutableListOf<ImageEntity>()

        for (imageId in imageIds) {
            val imageEntity = loadImage(imageId) ?: return null

            imageUris.add(imageEntity)
        }

        return imageUris
    }
    fun saveImage(rawImage: RawImage): ImageEntity?
    fun saveImages(rawImages: List<RawImage>): List<ImageEntity>? {
        val imageEntities = mutableListOf<ImageEntity>()

        for (rawImage in rawImages) {
            val imageEntity = saveImage(rawImage) ?: return null

            imageEntities.add(imageEntity)
        }

        return imageEntities
    }
    fun getImageDataByUri(imageUri: Uri): RawImage?
}