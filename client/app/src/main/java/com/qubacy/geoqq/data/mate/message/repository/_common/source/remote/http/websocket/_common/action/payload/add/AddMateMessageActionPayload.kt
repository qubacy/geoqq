package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.payload.add

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AddMateMessageActionPayload(
    @Json(name = CHAT_ID_PROP_NAME) val chatId: Long,
    @Json(name = TEXT_PROP_NAME) val text: String
) : PacketPayload {
    companion object {
        const val CHAT_ID_PROP_NAME = "chat-id"
        const val TEXT_PROP_NAME = "text"
    }
}