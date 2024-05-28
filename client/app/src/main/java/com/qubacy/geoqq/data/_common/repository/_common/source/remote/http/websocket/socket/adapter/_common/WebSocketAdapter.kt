package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler._common.WebSocketEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client._common.ClientEventJsonMiddleware

interface WebSocketAdapter {
    fun generateBaseEventHandlers(): Array<WebSocketEventHandler<WebSocketEvent>>
    fun addEventListener(eventListener: WebSocketEventListener)
    fun removeEventListener(eventListener: WebSocketEventListener)
    //fun pushAction(type: String, payloadString: String)
    fun sendAction(action: PackagedAction)
    fun isOpen(): Boolean
    fun open()
    fun close()
    fun getJsonMiddlewaresForClientEvent(type: String): List<ClientEventJsonMiddleware>
    fun processEvent(event: WebSocketEvent)
}