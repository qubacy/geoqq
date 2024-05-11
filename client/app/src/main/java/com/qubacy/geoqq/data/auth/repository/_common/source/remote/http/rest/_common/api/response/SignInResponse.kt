package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignInResponse(
    @Json(name = "access-token") val accessToken: String,
    @Json(name = "refresh-token") val refreshToken: String
) {

}