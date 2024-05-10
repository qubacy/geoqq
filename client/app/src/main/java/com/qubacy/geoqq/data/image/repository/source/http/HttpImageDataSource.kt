package com.qubacy.geoqq.data.image.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.image.repository.source.http.api.HttpImageDataSourceApi
import com.qubacy.geoqq.data.image.repository.source.http.api.request.GetImagesRequest
import com.qubacy.geoqq.data.image.repository.source.http.api.request.UploadImageRequest
import com.qubacy.geoqq.data.image.repository.source.http.api.request.UploadImageRequestImage
import com.qubacy.geoqq.data.image.repository.source.http.api.response.GetImageResponse
import com.qubacy.geoqq.data.image.repository.source.http.api.response.GetImagesResponse
import com.qubacy.geoqq.data.image.repository.source.http.api.response.UploadImageResponse
import javax.inject.Inject

class HttpImageDataSource @Inject constructor(
    private val mHttpImageDataSourceApi: HttpImageDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    fun getImage(id: Long): GetImageResponse {
        val getImageCall = mHttpImageDataSourceApi.getImage(id)
        val getImageResponse = mHttpCallExecutor.executeNetworkRequest(getImageCall)

        return getImageResponse
    }

    fun getImages(ids: List<Long>): GetImagesResponse {
        val getImagesRequest = GetImagesRequest(ids)
        val getImagesCall = mHttpImageDataSourceApi.getImages(getImagesRequest)
        val getImagesResponse = mHttpCallExecutor.executeNetworkRequest(getImagesCall)

        return getImagesResponse
    }

    fun uploadImage(
        extension: Int,
        base64Content: String
    ): UploadImageResponse {
        val uploadImageRequestContent = UploadImageRequestImage(extension, base64Content)
        val uploadImageRequest = UploadImageRequest(uploadImageRequestContent)
        val uploadImageCall = mHttpImageDataSourceApi.uploadImage(uploadImageRequest)
        val uploadImageResponse = mHttpCallExecutor.executeNetworkRequest(uploadImageCall)

        return uploadImageResponse
    }
}