package com.qubacy.geoqq.data.signin.repository.source.network.model.request

import com.squareup.moshi.JsonClass

@Deprecated("A JSON-like request has been tossed off for the request.")
@JsonClass(generateAdapter = true)
data class SignInWithUsernamePasswordRequestBody(
    val login: String,
    val password: String
) {

}