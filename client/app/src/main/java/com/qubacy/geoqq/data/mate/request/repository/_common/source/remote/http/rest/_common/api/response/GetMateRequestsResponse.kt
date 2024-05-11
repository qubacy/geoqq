package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response

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