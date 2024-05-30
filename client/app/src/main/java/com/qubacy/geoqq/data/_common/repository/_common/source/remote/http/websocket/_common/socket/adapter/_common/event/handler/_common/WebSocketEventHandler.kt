package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent

interface WebSocketEventHandler {
    fun handle(event: WebSocketEvent): Boolean
}