package com.qubacy.geoqq.data.token.repository.source.http.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateTokensResponse(
    @Json(name = "access-token") val accessToken: String,
    @Json(name = "refresh-token") val refreshToken: String
) {

}