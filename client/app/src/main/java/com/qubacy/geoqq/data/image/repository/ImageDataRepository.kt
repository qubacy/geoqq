package com.qubacy.geoqq.data.image.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.image.repository.error.ImageErrorEnum
import com.qubacy.geoqq.data.image.repository.result.DownloadImageResult
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.image.repository.result.SaveImageResult
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.NetworkImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.response.GetImageResponse
import retrofit2.Call
import java.io.IOException
import java.net.SocketException

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

    private suspend fun downloadImage(imageId: Long): Result {
        var response: retrofit2.Response<GetImageResponse>? = null

        try {
            mCurrentNetworkRequest = networkImageDataSource
                .getImage(imageId) as Call<Response>
            response = mCurrentNetworkRequest!!
                .execute() as retrofit2.Response<GetImageResponse>

        } catch (e: SocketException) { return InterruptionResult()
        } catch (e: IOException) {
            return ErrorResult(NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_FAILURE.error)
        }

        val error = retrieveNetworkError(response as retrofit2.Response<Response>)

        if (error != null) return ErrorResult(error)

        val responseBody = response.body()!!

        return DownloadImageResult(responseBody.imageContent)
    }

    private suspend fun saveImage(imageId: Long, image: Bitmap): Result {
        val uri = localImageDataSource.saveImageOnDevice(getImageTitleFromImageId(imageId), image)

        if (uri == null) return ErrorResult(ImageErrorEnum.IMAGE_SAVING_FAILED.error)

        return SaveImageResult(uri)
    }

    suspend fun getImage(imageId: Long): Result {
        var localImageUri: Uri? = null

        try {
            localImageUri = localImageDataSource.loadImage(getImageTitleFromImageId(imageId))

        } catch (e: Exception) { return ErrorResult(ImageErrorEnum.IMAGE_LOADING_FAILED.error) }

        if (localImageUri != null) return GetImageResult(localImageUri)

        val imageDownloadResult = downloadImage(imageId)

        if (imageDownloadResult is ErrorResult) return imageDownloadResult

        val imageBytes = StringEncodingDecodingUtil
            .base64StringAsBytes((imageDownloadResult as DownloadImageResult).imageContent)

        if (imageBytes == null) return ErrorResult(ImageErrorEnum.IMAGE_DECODING_FAILED.error)

        val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        return saveImage(imageId, imageBitmap)
    }

}