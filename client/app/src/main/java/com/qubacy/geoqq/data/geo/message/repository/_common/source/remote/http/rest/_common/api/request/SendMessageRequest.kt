package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SendMessageRequest(
    @Json(name = "text") val text: String,
    @Json(name = "radius") val radius: Int,
    @Json(name = "longitude") val longitude: Float,
    @Json(name = "latitude") val latitude: Float
) {

}