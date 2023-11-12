package com.qubacy.geoqq.data.mate.request.repository.source.network.model.common

import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MateRequest(
    val id: Long,
    @Json(name = "user-id") val userId: Long
) {

}

fun MateRequest.toDataMateRequest(): DataMateRequest {
    return DataMateRequest(id, userId)
}