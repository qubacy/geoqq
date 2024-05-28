package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.message.error.callback

import com.qubacy.geoqq._common.model.error._common.Error

interface WebSocketErrorMessageEventHandlerCallback {
    fun retryActionSending()
    fun shutdownWebSocketWithError(error: Error)
}