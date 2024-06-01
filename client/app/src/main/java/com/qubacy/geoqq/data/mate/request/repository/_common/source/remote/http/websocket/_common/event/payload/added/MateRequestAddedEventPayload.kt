package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MateRequestAddedEventPayload(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = USER_ID_PROP_NAME) val userId: Long
) {
    companion object {
        const val ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user-id"
    }
}