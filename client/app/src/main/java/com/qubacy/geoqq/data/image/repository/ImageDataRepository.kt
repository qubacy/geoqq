package com.qubacy.geoqq.data.image.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.flowable.FlowableDataRepository
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.image.repository.result.DownloadImageResult
import com.qubacy.geoqq.data.image.repository.result.GetImageByUriResult
import com.qubacy.geoqq.data.image.repository.result.GetImageIdByUriResult
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.image.repository.result.GetImageWithNetworkResult
import com.qubacy.geoqq.data.image.repository.result.LoadImageResult
import com.qubacy.geoqq.data.image.repository.result.SaveImageResult
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.NetworkImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.response.GetImageResponse
import retrofit2.Call

class ImageDataRepository(
    val localImageDataSource: LocalImageDataSource,
    val networkImageDataSource: NetworkImageDataSource
) : FlowableDataRepository() {
    private fun downloadImage(imageId: Long, accessToken: String): Result {
        val networkCall = networkImageDataSource
            .getImage(imageId, accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetImageResponse

        return DownloadImageResult(responseBody.imageContent)
    }

    private fun loadImage(imageId: Long): Result {
        var localImageUri: Uri? = null

        try {
            localImageUri = localImageDataSource.loadImage(imageId)

        } catch (e: Exception) { return ErrorResult(ErrorContext.Image.IMAGE_LOADING_FAILED.id) }

        return LoadImageResult(localImageUri)
    }

    private fun saveImage(imageId: Long, image: Bitmap): Result {
        val uri = localImageDataSource.saveImageOnDevice(imageId, image)

        if (uri == null) return ErrorResult(ErrorContext.Image.IMAGE_SAVING_FAILED.id)

        return SaveImageResult(uri)
    }

    private fun getImageWithNetwork(
        imageId: Long, accessToken: String
    ): Result {
        val imageDownloadResult = downloadImage(imageId, accessToken)

        if (imageDownloadResult is ErrorResult) return imageDownloadResult

        val imageBytes = StringEncodingDecodingUtil
            .base64StringAsBytes((imageDownloadResult as DownloadImageResult).imageContent)

        if (imageBytes == null) return ErrorResult(ErrorContext.Image.IMAGE_DECODING_FAILED.id)

        val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val saveImageResult = saveImage(imageId, imageBitmap)

        if (saveImageResult is ErrorResult) return saveImageResult

        val saveImageResultCast = saveImageResult as SaveImageResult

        return GetImageWithNetworkResult(saveImageResultCast.imageUri)
    }

    private suspend fun getImageWithNetworkForUpdate(imageId: Long, accessToken: String) {
        val getImageWithNetworkResult = getImageWithNetwork(imageId, accessToken)

        if (getImageWithNetworkResult is ErrorResult) emitResult(getImageWithNetworkResult)

        val getImageWithNetworkResultCast = getImageWithNetworkResult as GetImageWithNetworkResult

        emitResult(GetImageResult(getImageWithNetworkResultCast.imageUri))
    }

    private fun getImageWithNetworkForResult(
        imageId: Long, accessToken: String
    ): Result {
        return getImageWithNetwork(imageId, accessToken)
    }

    suspend fun getImage(imageId: Long, accessToken: String, isLatest: Boolean = true): Result {
        val imageLoadResult = loadImage(imageId)

        if (imageLoadResult is ErrorResult) return imageLoadResult

        val imageLoadResultCast = imageLoadResult as LoadImageResult

        if (imageLoadResultCast.imageUri != null) {
            if (isLatest) getImageWithNetworkForUpdate(imageId, accessToken)

            return GetImageResult(imageLoadResultCast.imageUri)
        }

        val getImageWithNetworkResult = getImageWithNetworkForResult(imageId, accessToken)

        if (getImageWithNetworkResult is ErrorResult) return getImageWithNetworkResult
        if (getImageWithNetworkResult is InterruptionResult) return getImageWithNetworkResult

        val getImageWithNetworkResultCast = getImageWithNetworkResult as GetImageWithNetworkResult

        return GetImageResult(getImageWithNetworkResultCast.imageUri)
    }

    fun getImageByUri(imageUri: Uri): Result {
        val imageBitmap = localImageDataSource.getImageBitmapByUri(imageUri)
            ?: return ErrorResult(ErrorContext.Image.IMAGE_LOADING_FAILED.id)

        return GetImageByUriResult(imageBitmap)
    }

    fun getImageIdByUri(imageUri: Uri): Result {
        val imageId = localImageDataSource.getImageIdByUri(imageUri)
            ?: return ErrorResult(ErrorContext.Image.IMAGE_LOADING_FAILED.id)

        return GetImageIdByUriResult(imageId)
    }
}