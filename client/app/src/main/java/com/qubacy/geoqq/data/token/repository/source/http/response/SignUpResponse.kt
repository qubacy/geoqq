package com.qubacy.geoqq.data.token.repository.source.http.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SignUpResponse(
    @Json(name = "access-token") val accessToken: String,
    @Json(name = "refresh-token") val refreshToken: String
) {

}