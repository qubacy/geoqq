package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.message.MessageEventPayload
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MateMessageAddedEventPayload(
    id: Long,
    text: String,
    time: Long,
    userId: Long,
    @Json(name = CHAT_ID_PROP_NAME) val chatId: Long
) : MessageEventPayload(id, text, time, userId) {
    companion object {
        const val CHAT_ID_PROP_NAME = "chat-id"
    }
}