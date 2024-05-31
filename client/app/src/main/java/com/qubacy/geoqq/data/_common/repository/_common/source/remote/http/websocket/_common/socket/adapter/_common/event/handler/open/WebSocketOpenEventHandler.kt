package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.open

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler._common.WebSocketEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.open.callback.WebSocketOpenEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.open.WebSocketOpenEvent
import javax.inject.Inject

class WebSocketOpenEventHandler @Inject constructor() : WebSocketEventHandler {
    private lateinit var mCallback: WebSocketOpenEventHandlerCallback

    fun setCallback(callback: WebSocketOpenEventHandlerCallback) {
        mCallback = callback
    }

    override fun handle(event: WebSocketEvent): Boolean {
        if (event !is WebSocketOpenEvent) return false

        mCallback.onWebSocketOpen()

        return true
    }
}