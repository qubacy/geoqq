package com.qubacy.geoqq.data.image.repository

import android.net.Uri
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.error.type.DataImageLocalErrorType
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.model.toDataImage
import com.qubacy.geoqq.data.image.repository._common.toRawImage
import com.qubacy.geoqq.data.image.repository._common.toUploadImageRequest
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import com.qubacy.geoqq.data.image.repository.source.http.request.GetImagesRequest
import com.qubacy.geoqq.data.image.repository.source.http.request.UploadImageRequest
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import javax.inject.Inject

class ImageDataRepository @Inject constructor(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mTokenDataRepository: TokenDataRepository,
    private val mLocalImageDataSource: LocalImageDataSource,
    private val mHttpImageDataSource: HttpImageDataSource,
    private val mHttpCallExecutor: HttpCallExecutor
) : DataRepository {
    suspend fun getImageById(imageId: Long): DataImage {
        val localImage = mLocalImageDataSource.loadImage(imageId)

        if (localImage != null) return localImage.toDataImage()

        val accessToken = mTokenDataRepository.getTokens().accessToken

        val getImageCall = mHttpImageDataSource.getImage(imageId, accessToken)
        val getImageResponse = mHttpCallExecutor.executeNetworkRequest(getImageCall)

        val httpImageToSave = getImageResponse.toRawImage()

        val savedImage = mLocalImageDataSource.saveImage(httpImageToSave)

        if (savedImage == null)
            throw ErrorAppException(mErrorDataRepository.getError(
                DataImageLocalErrorType.SAVING_FAILED.getErrorCode()))

        return savedImage.toDataImage()
    }

    // todo: isn't it drastic for overall performance?:
    suspend fun getImagesByIds(imagesIds: List<Long>): List<DataImage> {
        val localImages = mLocalImageDataSource.loadImages(imagesIds)

        if (localImages != null) return localImages.map { it.toDataImage() }

        val accessToken = mTokenDataRepository.getTokens().accessToken

        val getImagesRequest = GetImagesRequest(accessToken, imagesIds)
        val getImagesCall = mHttpImageDataSource.getImages(getImagesRequest)
        val getImagesResponse = mHttpCallExecutor.executeNetworkRequest(getImagesCall)

        val httpImagesToSave = getImagesResponse.images.map { it.toRawImage() }

        val savedImages = mLocalImageDataSource.saveImages(httpImagesToSave)

        if (savedImages == null)
            throw ErrorAppException(mErrorDataRepository.getError(
                DataImageLocalErrorType.SAVING_FAILED.getErrorCode()))

        return savedImages.map { it.toDataImage() }
    }

    suspend fun saveImage(imageUri: Uri): DataImage {
        val imageData = mLocalImageDataSource.getImageDataByUri(imageUri)

        if (imageData == null)
            throw ErrorAppException(mErrorDataRepository.getError(
                DataImageLocalErrorType.LOADING_DATA_FAILED.getErrorCode()))

        val accessToken = mTokenDataRepository.getTokens().accessToken

        val imageContentToUpload = imageData.toUploadImageRequest()
        val uploadImageRequest = UploadImageRequest(accessToken, imageContentToUpload)
        val uploadImageCall = mHttpImageDataSource.uploadImage(uploadImageRequest)
        val uploadImageResponse = mHttpCallExecutor.executeNetworkRequest(uploadImageCall)

        val localImageToSave = imageData.copy(id = uploadImageResponse.id)
        val savedImage = mLocalImageDataSource.saveImage(localImageToSave)

        if (savedImage == null)
            throw ErrorAppException(mErrorDataRepository.getError(
                DataImageLocalErrorType.SAVING_FAILED.getErrorCode()))

        return savedImage.toDataImage()
    }
}