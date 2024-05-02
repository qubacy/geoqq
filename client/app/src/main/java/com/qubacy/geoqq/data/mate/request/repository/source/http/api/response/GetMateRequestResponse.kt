package com.qubacy.geoqq.data.mate.request.repository.source.http.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMateRequestResponse(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = USER_ID_PROP_NAME) val userId: Long
) {
    companion object {
        const val ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user-id"
    }
}