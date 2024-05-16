package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.container

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.listener.WebSocketListenerAdapter
import okhttp3.WebSocket

class WebSocketInitContainer(
    val webSocket: WebSocket,
    val webSocketListenerAdapter: WebSocketListenerAdapter
) {

}