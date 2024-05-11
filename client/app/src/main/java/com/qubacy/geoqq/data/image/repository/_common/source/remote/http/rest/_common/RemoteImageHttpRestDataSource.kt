package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.GetImageResponse
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.GetImagesResponse
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.UploadImageResponse

interface RemoteImageHttpRestDataSource {
    fun getImage(id: Long): GetImageResponse
    fun getImages(ids: List<Long>): GetImagesResponse
    fun uploadImage(extension: Int, base64Content: String): UploadImageResponse
}