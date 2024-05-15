package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.listener

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model._common.WebSocketEvent

interface WebSocketEventListener {
    fun onEventGotten(event: WebSocketEvent)
}