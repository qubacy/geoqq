package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client._common.ClientEventJsonMiddleware

interface WebSocketAdapter {
    fun addEventListener(eventListener: WebSocketEventListener)
    fun removeEventListener(eventListener: WebSocketEventListener)
    fun sendEvent(type: String, payloadString: String)
    fun close()
    fun getJsonMiddlewaresForClientEvent(type: String): List<ClientEventJsonMiddleware>
}