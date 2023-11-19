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
import com.qubacy.geoqq.data.image.repository.result.DownloadImagesResult
import com.qubacy.geoqq.data.image.repository.result.GetImageByUriResult
import com.qubacy.geoqq.data.image.repository.result.GetImagesIdsByUrisResult
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.image.repository.result.GetImagesWithNetworkResult
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.image.repository.result.LoadImageResult
import com.qubacy.geoqq.data.image.repository.result.LoadImagesResult
import com.qubacy.geoqq.data.image.repository.result.SaveImageResult
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.NetworkImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.model.request.GetImagesRequestBody
import com.qubacy.geoqq.data.image.repository.source.network.model.response.GetImagesResponse
import retrofit2.Call

class ImageDataRepository(
    val localImageDataSource: LocalImageDataSource,
    val networkImageDataSource: NetworkImageDataSource
) : FlowableDataRepository() {
    private fun downloadImages(imagesIds: List<Long>, accessToken: String): Result {
        val getImagesRequestBody = GetImagesRequestBody(accessToken, imagesIds)
        val networkCall = networkImageDataSource.getImages(getImagesRequestBody) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetImagesResponse

        return DownloadImagesResult(responseBody.images)
    }

    private fun loadImage(imageId: Long): Result {
        var localImageUri: Uri? = null

        try {
            localImageUri = localImageDataSource.loadImage(imageId)

        } catch (e: Exception) { return ErrorResult(ErrorContext.Image.IMAGE_LOADING_FAILED.id) }

        return LoadImageResult(localImageUri)
    }

    private fun loadImages(imagesIds: List<Long>): Result {
        val imageIdToImageUriMap = mutableMapOf<Long, Uri>()
        val notFoundImagesIds = mutableListOf<Long>()

        for (imageId in imagesIds) {
            val loadImageResult = loadImage(imageId)

            if (loadImageResult is ErrorResult) return loadImageResult

            val imageUri = (loadImageResult as LoadImageResult).imageUri

            if (imageUri != null) imageIdToImageUriMap[imageId] = imageUri
            else notFoundImagesIds.add(imageId)
        }

        return LoadImagesResult(imageIdToImageUriMap, notFoundImagesIds)
    }

    private fun saveImage(imageId: Long, image: Bitmap): Result {
        val uri = localImageDataSource.saveImageOnDevice(imageId, image)

        if (uri == null) return ErrorResult(ErrorContext.Image.IMAGE_SAVING_FAILED.id)

        return SaveImageResult(uri)
    }

    private fun getImagesWithNetwork(
        imagesIds: List<Long>, accessToken: String
    ): Result {
        val downloadImagesResult = downloadImages(imagesIds, accessToken)

        if (downloadImagesResult is ErrorResult) return downloadImagesResult

        val downloadImagesResultCast = downloadImagesResult as DownloadImagesResult
        val savedImageIdToUriMap = mutableMapOf<Long, Uri>()

        for (downloadedImage in downloadImagesResultCast.images) {
            val imageBytes = StringEncodingDecodingUtil
                .base64StringAsBytes(downloadedImage.imageContent)

            if (imageBytes == null) return ErrorResult(ErrorContext.Image.IMAGE_DECODING_FAILED.id)

            val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val saveImageResult = saveImage(downloadedImage.id, imageBitmap)

            if (saveImageResult is ErrorResult) return saveImageResult

            val saveImageResultCast = saveImageResult as SaveImageResult

            savedImageIdToUriMap[downloadedImage.id] = saveImageResultCast.imageUri
        }

        return GetImagesWithNetworkResult(savedImageIdToUriMap)
    }

    private fun getImagesWithNetworkForResult(
        imagesUris: List<Long>, accessToken: String
    ): Result {
        return getImagesWithNetwork(imagesUris, accessToken)
    }

    suspend fun getImages(
        imagesIds: List<Long>,
        accessToken: String
    ): Result {
        val loadImagesResult = loadImages(imagesIds)

        if (loadImagesResult is ErrorResult) return loadImagesResult

        val loadImagesResultCast = loadImagesResult as LoadImagesResult

        // todo: it'd be better to return a DEFAULT IMAGE URI instead of NULL..
        if (loadImagesResultCast.imageIdToImageUriMap.size == imagesIds.size) {
            return GetImagesResult(loadImagesResultCast.imageIdToImageUriMap, true)
        }

        val getImagesWithNetworkResult = getImagesWithNetworkForResult(
            loadImagesResultCast.notFoundImagesIds, accessToken)

        if (getImagesWithNetworkResult is ErrorResult) return getImagesWithNetworkResult
        if (getImagesWithNetworkResult is InterruptionResult) return getImagesWithNetworkResult

        val getImagesWithNetworkResultCast = getImagesWithNetworkResult as GetImagesWithNetworkResult
        val resultImageIdToImageUriMap = mutableMapOf<Long, Uri>().apply {
            putAll(loadImagesResultCast.imageIdToImageUriMap)
            putAll(getImagesWithNetworkResultCast.imageIdToUriMap)
        }

        return GetImagesResult(resultImageIdToImageUriMap, false)
    }

    fun getImageByUri(imageUri: Uri): Result {
        val imageBitmap = localImageDataSource.getImageBitmapByUri(imageUri)
            ?: return ErrorResult(ErrorContext.Image.IMAGE_LOADING_FAILED.id)

        return GetImageByUriResult(imageBitmap)
    }

    fun getImagesIdsByUris(imagesUris: List<Uri>): Result {
        val imagesIds = mutableListOf<Long>()

        for (imageUri in imagesUris) {
            val imageId = localImageDataSource.getImageIdByUri(imageUri)
                ?: return ErrorResult(ErrorContext.Image.IMAGE_LOADING_FAILED.id)

            imagesIds.add(imageId)
        }

        return GetImagesIdsByUrisResult(imagesIds)
    }
}