package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PostMateRequestRequest(
    @Json(name = "user-id") val userId: Long
) {

}