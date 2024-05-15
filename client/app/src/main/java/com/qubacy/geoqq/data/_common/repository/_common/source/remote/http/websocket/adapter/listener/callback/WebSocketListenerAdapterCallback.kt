package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.listener.callback

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model._common.WebSocketEvent

interface WebSocketListenerAdapterCallback {
    fun onEventGotten(event: WebSocketEvent)
}