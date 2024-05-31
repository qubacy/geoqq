package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.error

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.error.type.DataHttpWebSocketErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler._common.WebSocketEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.error.callback.WebSocketErrorEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import javax.inject.Inject

class WebSocketErrorEventHandler @Inject constructor() : WebSocketEventHandler {
    private lateinit var mCallback: WebSocketErrorEventHandlerCallback

    fun setCallback(callback: WebSocketErrorEventHandlerCallback) {
        mCallback = callback
    }

    override fun handle(event: WebSocketEvent): Boolean {
        if (event !is WebSocketErrorEvent) return false

        when (event.error.id) {
            DataHttpWebSocketErrorType.WEB_SOCKET_FAILURE.getErrorCode() ->
                processWebSocketFailureError(event.error)
            else -> return false
        }

        return true
    }

    private fun processWebSocketFailureError(error: Error) {
        mCallback.reconnectToWebSocket()
    }
}