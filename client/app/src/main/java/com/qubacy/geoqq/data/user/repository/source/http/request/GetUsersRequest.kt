package com.qubacy.geoqq.data.user.repository.source.http.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetUsersRequest(
    @Json(name = "access-token") val accessToken: String,
    val ids: List<Long>
) {

}