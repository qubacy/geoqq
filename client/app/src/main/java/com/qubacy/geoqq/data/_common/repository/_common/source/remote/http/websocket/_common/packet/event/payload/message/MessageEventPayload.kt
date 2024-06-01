package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class MessageEventPayload(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = TEXT_PROP_NAME) val text: String,
    @Json(name = TIME_PROP_NAME) val time: Long,
    @Json(name = USER_ID_PROP_NAME) val userId: Long
) {
    companion object {
        const val ID_PROP_NAME = "id"
        const val TEXT_PROP_NAME = "text"
        const val TIME_PROP_NAME = "time"
        const val USER_ID_PROP_NAME = "user-id"
    }
}