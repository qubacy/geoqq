package com.qubacy.geoqq.data.mate.request.repository.source.http.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMateRequestCountResponse(
    @Json(name = COUNT_PROP_NAME) val count: Int
) {
    companion object {
        const val COUNT_PROP_NAME = "count"
    }
}