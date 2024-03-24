package com.qubacy.geoqq.data.image.repository

import android.net.Uri
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.error.type.ImageErrorType
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import com.qubacy.geoqq.data.image.repository.source.http.request.GetImagesRequest
import com.qubacy.geoqq.data.image.repository.source.http.request.UploadImageRequest
import com.qubacy.geoqq.data.image.repository.source.http.request.UploadImageRequestImage
import com.qubacy.geoqq.data.image.repository.source.http.response.toRawImage
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.local.entity.toDataImage
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import javax.inject.Inject

class ImageDataRepository @Inject constructor(
    val errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val localImageDataSource: LocalImageDataSource,
    val httpImageDataSource: HttpImageDataSource
) : DataRepository {
    suspend fun getImageById(imageId: Long): DataImage {
        val localImage = localImageDataSource.loadImage(imageId)

        if (localImage != null) return localImage.toDataImage()

        val accessToken = tokenDataRepository.getTokens().accessToken

        val getImagesCall = httpImageDataSource.getImage(imageId, accessToken)
        val getImagesResponse = executeNetworkRequest(errorDataRepository, getImagesCall)

        val httpImageToSave =  getImagesResponse.toRawImage()

        val savedImage = localImageDataSource.saveImage(httpImageToSave)

        if (savedImage == null)
            throw ErrorAppException(errorDataRepository.getError(
                ImageErrorType.SAVING_FAILED.getErrorCode()))

        return savedImage.toDataImage()
    }

    // todo: isn't it drastic for overall performance?:
    suspend fun getImagesByIds(imagesIds: List<Long>): List<DataImage> {
        val localImages = localImageDataSource.loadImages(imagesIds)

        if (localImages != null) return localImages.map { it.toDataImage() }

        val accessToken = tokenDataRepository.getTokens().accessToken

        val getImagesRequest = GetImagesRequest(accessToken, imagesIds)
        val getImagesCall = httpImageDataSource.getImages(getImagesRequest)
        val getImagesResponse = executeNetworkRequest(errorDataRepository, getImagesCall)

        val httpImagesToSave = getImagesResponse.images.map { it.toRawImage() }

        val savedImages = localImageDataSource.saveImages(httpImagesToSave)

        if (savedImages == null)
            throw ErrorAppException(errorDataRepository.getError(
                ImageErrorType.SAVING_FAILED.getErrorCode()))

        return savedImages.map { it.toDataImage() }
    }

    suspend fun saveImage(imageUri: Uri): DataImage {
        val imageData = localImageDataSource.getImageDataByUri(imageUri)

        if (imageData == null)
            throw ErrorAppException(errorDataRepository.getError(
                ImageErrorType.LOADING_DATA_FAILED.getErrorCode()))

        val accessToken = tokenDataRepository.getTokens().accessToken

        val imageContentToUpload = UploadImageRequestImage.create(imageData)
        val uploadImageRequest = UploadImageRequest(accessToken, imageContentToUpload)
        val uploadImageCall = httpImageDataSource.uploadImage(uploadImageRequest)
        val uploadImageResponse = executeNetworkRequest(errorDataRepository, uploadImageCall)

        val localImageToSave = imageData.copy(id = uploadImageResponse.id)
        val savedImage = localImageDataSource.saveImage(localImageToSave)

        if (savedImage == null)
            throw ErrorAppException(errorDataRepository.getError(
                ImageErrorType.SAVING_FAILED.getErrorCode()))

        return savedImage.toDataImage()
    }
}