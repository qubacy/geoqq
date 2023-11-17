package com.qubacy.geoqq.data.image.repository.source.network.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImagesRequestBody(
    @Json(name = "access-token") val accessToken: String,
    val ids: List<Long>
) {

}