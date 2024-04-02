package com.qubacy.geoqq.data.myprofile.repository.source.http.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DeleteMyProfileRequest(
    @Json(name = "accessToken") val accessToken: String
) {

}