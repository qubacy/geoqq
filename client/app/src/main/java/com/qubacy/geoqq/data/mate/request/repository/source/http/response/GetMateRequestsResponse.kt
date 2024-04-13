package com.qubacy.geoqq.data.mate.request.repository.source.http.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMateRequestsResponse(
    @Json(name = REQUESTS_PROP_NAME) val requests: List<GetMateRequestResponse>
) {
    companion object {
        const val REQUESTS_PROP_NAME = "requests"
    }
}