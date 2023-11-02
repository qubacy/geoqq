package com.qubacy.geoqq.data.image.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.image.error.ImageErrorEnum
import com.qubacy.geoqq.data.image.repository.result.DownloadImageResult
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.image.repository.result.LoadImageResult
import com.qubacy.geoqq.data.image.repository.result.SaveImageResult
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.NetworkImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.response.GetImageResponse
import retrofit2.Call

class ImageDataRepository(
    val localImageDataSource: LocalImageDataSource,
    val networkImageDataSource: NetworkImageDataSource
) : NetworkDataRepository() {
    companion object {
        const val IMAGE_PREFIX = "image"
    }

    private fun getImageTitleFromImageId(imageId: Long): String {
        return (IMAGE_PREFIX + imageId.toString())
    }

    private fun downloadImage(imageId: Long, accessToken: String): Result {
        val networkCall = networkImageDataSource
            .getImage(imageId, accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response.body()!! as GetImageResponse

        return DownloadImageResult(responseBody.imageContent)
    }

    private fun loadImage(imageId: Long): Result {
        var localImageUri: Uri? = null

        try {
            localImageUri = localImageDataSource.loadImage(getImageTitleFromImageId(imageId))

        } catch (e: Exception) { return ErrorResult(ImageErrorEnum.IMAGE_LOADING_FAILED.error) }

        return LoadImageResult(localImageUri)
    }

    private fun saveImage(imageId: Long, image: Bitmap): Result {
        val uri = localImageDataSource.saveImageOnDevice(getImageTitleFromImageId(imageId), image)

        if (uri == null) return ErrorResult(ImageErrorEnum.IMAGE_SAVING_FAILED.error)

        return SaveImageResult(uri)
    }

    suspend fun getImage(imageId: Long, accessToken: String): Result {
        val imageLoadResult = loadImage(imageId)

        if (imageLoadResult is ErrorResult) return imageLoadResult

        val imageLoadResultCast = imageLoadResult as LoadImageResult

        if (imageLoadResultCast.imageUri != null)
            return GetImageResult(imageLoadResultCast.imageUri)

        val imageDownloadResult = downloadImage(imageId, accessToken)

        if (imageDownloadResult is ErrorResult) return imageDownloadResult

        val imageBytes = StringEncodingDecodingUtil
            .base64StringAsBytes((imageDownloadResult as DownloadImageResult).imageContent)

        if (imageBytes == null) return ErrorResult(ImageErrorEnum.IMAGE_DECODING_FAILED.error)

        val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val saveImageResult = saveImage(imageId, imageBitmap)

        if (saveImageResult is ErrorResult) return saveImageResult

        val saveImageResultCast = saveImageResult as SaveImageResult

        return GetImageResult(saveImageResultCast.imageUri)
    }

}