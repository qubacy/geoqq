package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model._common.WebSocketEvent

interface WebSocketEventHandler<WebSocketEventType : WebSocketEvent> {
    fun handle(event: WebSocketEventType): Boolean
}