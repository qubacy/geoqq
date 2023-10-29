package com.qubacy.geoqq.data.signin.repository.source.network.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Deprecated("A JSON-like request has been tossed off for the request.")
@JsonClass(generateAdapter = true)
class SignInWithRefreshTokenRequestBody(
    @Json(name = "refresh-token") val refreshToken: String
) {

}