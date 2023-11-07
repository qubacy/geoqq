package com.qubacy.geoqq.data.myprofile.repository.source.network.model.request.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Security(
    val password: String,
    @Json(name = "new-password") val newPassword: String
) {

}