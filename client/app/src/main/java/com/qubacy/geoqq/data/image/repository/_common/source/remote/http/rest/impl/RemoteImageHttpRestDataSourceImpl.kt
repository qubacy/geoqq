package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.RemoteImageHttpRestDataSource
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.RemoteImageHttpRestDataSourceApi
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.request.GetImagesRequest
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.request.UploadImageRequest
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.request.UploadImageRequestImage
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.GetImageResponse
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.GetImagesResponse
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.UploadImageResponse
import javax.inject.Inject

class RemoteImageHttpRestDataSourceImpl @Inject constructor(
    private val mHttpImageDataSourceApi: RemoteImageHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) : RemoteImageHttpRestDataSource {
    override fun getImage(id: Long): GetImageResponse {
        val getImageCall = mHttpImageDataSourceApi.getImage(id)
        val getImageResponse = mHttpCallExecutor.executeNetworkRequest(getImageCall)

        return getImageResponse
    }

    override fun getImages(ids: List<Long>): GetImagesResponse {
        val getImagesRequest = GetImagesRequest(ids)
        val getImagesCall = mHttpImageDataSourceApi.getImages(getImagesRequest)
        val getImagesResponse = mHttpCallExecutor.executeNetworkRequest(getImagesCall)

        return getImagesResponse
    }

    override fun uploadImage(
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