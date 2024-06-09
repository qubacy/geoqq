package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.general.error

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.ErrorEventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message._common.WebSocketMessageEvent
import kotlin.reflect.full.isSubclassOf

class WebSocketErrorMessageEvent(
    val event: String,
    val payload: ErrorEventPayload
) : WebSocketMessageEvent() {
    override fun equals(other: Any?): Boolean {
        if (other == null || !other::class.isSubclassOf(WebSocketErrorMessageEvent::class))
            return false

        other as WebSocketErrorMessageEvent

        return (event == other.event && payload == other.payload)
    }

    override fun hashCode(): Int {
        var result = event.hashCode()

        result = 31 * result + payload.hashCode()

        return result
    }
}