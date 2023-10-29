package com.qubacy.geoqq.data.signin.repository.source.network.model.response

import com.qubacy.geoqq.data.common.repository.source.network.model.response.common.Response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SignInWithRefreshTokenResponse(
    @Json(name = "access-token") val accessToken: String,
    @Json(name = "refresh-token") val refreshToken: String
) : Response() {

}