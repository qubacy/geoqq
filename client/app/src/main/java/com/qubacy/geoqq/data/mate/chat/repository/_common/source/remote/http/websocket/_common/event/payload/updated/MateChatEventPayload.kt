package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.message.MessageEventPayload
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MateChatEventPayload(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = USER_ID_PROP_NAME) val userId: Long,
    @Json(name = NEW_MESSAGE_COUNT_PROP_NAME) val newMessageCount: Int,
    @Json(name = LAST_MESSAGE_PROP_NAME) val lastMessage: MessageEventPayload?
) : PacketPayload {
    companion object {
        const val ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user-id"
        const val NEW_MESSAGE_COUNT_PROP_NAME = "new-message-count"
        const val LAST_MESSAGE_PROP_NAME = "last-message"
    }
}