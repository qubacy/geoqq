package com.qubacy.geoqq.data.image.repository

import android.net.Uri
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.image.error.type.DataImageLocalErrorType
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.model.toDataImage
import com.qubacy.geoqq.data.image.repository._common.toExtensionBase64Content
import com.qubacy.geoqq.data.image.repository._common.toRawImage
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import javax.inject.Inject

class ImageDataRepository @Inject constructor(
    private val mErrorSource: LocalErrorDataSource,
    private val mLocalImageDataSource: LocalImageDataSource,
    private val mHttpImageDataSource: HttpImageDataSource
) : DataRepository {
    suspend fun getImageById(imageId: Long): DataImage {
        val localImage = mLocalImageDataSource.loadImage(imageId)

        if (localImage != null) return localImage.toDataImage()

        val getImageResponse = mHttpImageDataSource.getImage(imageId)

        val httpImageToSave = getImageResponse.toRawImage()

        val savedImage = mLocalImageDataSource.saveImage(httpImageToSave)

        if (savedImage == null)
            throw ErrorAppException(mErrorSource.getError(
                DataImageLocalErrorType.SAVING_FAILED.getErrorCode()))

        return savedImage.toDataImage()
    }

    // todo: isn't it drastic for overall performance?:
    suspend fun getImagesByIds(imagesIds: List<Long>): List<DataImage> {
        val localImages = mLocalImageDataSource.loadImages(imagesIds)

        if (localImages != null) return localImages.map { it.toDataImage() }

        val getImagesResponse = mHttpImageDataSource.getImages(imagesIds)

        val httpImagesToSave = getImagesResponse.images.map { it.toRawImage() }

        val savedImages = mLocalImageDataSource.saveImages(httpImagesToSave)

        if (savedImages == null)
            throw ErrorAppException(mErrorSource.getError(
                DataImageLocalErrorType.SAVING_FAILED.getErrorCode()))

        return savedImages.map { it.toDataImage() }
    }

    suspend fun saveImage(imageUri: Uri): DataImage {
        val imageData = mLocalImageDataSource.getImageDataByUri(imageUri)

        if (imageData == null)
            throw ErrorAppException(mErrorSource.getError(
                DataImageLocalErrorType.LOADING_DATA_FAILED.getErrorCode()))

        val imageExtensionBase64Content = imageData.toExtensionBase64Content()
        val uploadImageResponse = mHttpImageDataSource.uploadImage(
            imageExtensionBase64Content.first, imageExtensionBase64Content.second)

        val localImageToSave = imageData.copy(id = uploadImageResponse.id)
        val savedImage = mLocalImageDataSource.saveImage(localImageToSave)

        if (savedImage == null)
            throw ErrorAppException(mErrorSource.getError(
                DataImageLocalErrorType.SAVING_FAILED.getErrorCode()))

        return savedImage.toDataImage()
    }
}