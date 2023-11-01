package com.qubacy.geoqq.data.image.repository.source.network.response

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImageResponse(
//    val imageId: Long, ???
    @Json(name = "image-content") val imageContent: String
) : Response() {

}