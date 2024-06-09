package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error.callback

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.ErrorEventPayload

interface WebSocketErrorMessageEventHandlerCallback {
    fun conveyMessageError(type: String, payload: ErrorEventPayload)
    fun retryActionSending()
    fun shutdownWebSocketWithError(error: Error)
}