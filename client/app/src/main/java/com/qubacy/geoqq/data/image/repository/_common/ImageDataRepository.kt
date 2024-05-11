package com.qubacy.geoqq.data.image.repository._common

import android.net.Uri
import com.qubacy.geoqq.data.image.model.DataImage

interface ImageDataRepository {
    suspend fun getImageById(imageId: Long): DataImage
    suspend fun getImagesByIds(imagesIds: List<Long>): List<DataImage>
    suspend fun saveImage(imageUri: Uri): DataImage
}