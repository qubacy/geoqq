package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.listener.callback

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent

interface WebSocketListenerAdapterCallback {
    fun onEventGotten(event: WebSocketEvent)
}