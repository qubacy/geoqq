package com.qubacy.geoqq.data.signup.repository.source.network.response

import com.qubacy.geoqq.data.common.repository.source.network.model.response.common.Response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SignUpResponse(
    @Json(name = "access-token") val accessToken: String,
    @Json(name = "refresh-token") val refreshToken: String
) : Response() {

}