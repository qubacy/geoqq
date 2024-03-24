package com.qubacy.geoqq.data.image.repository.source.http.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImagesRequest(
    @Json(name = "access-token") val accessToken: String,
    val ids: List<Long>
) {

}