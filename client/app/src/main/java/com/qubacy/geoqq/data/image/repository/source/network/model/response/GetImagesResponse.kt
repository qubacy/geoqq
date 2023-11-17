package com.qubacy.geoqq.data.image.repository.source.network.model.response

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImagesResponse(
    val images: List<NetworkImageModel>
) : Response() {

}

@JsonClass(generateAdapter = true)
class NetworkImageModel(
    val id: Long,
    @Json(name = "content") val imageContent: String
) {

}