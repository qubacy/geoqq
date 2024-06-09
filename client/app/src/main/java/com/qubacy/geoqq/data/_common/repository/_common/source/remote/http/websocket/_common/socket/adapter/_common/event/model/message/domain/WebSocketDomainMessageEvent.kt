package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.domain

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message._common.WebSocketMessageEvent

class WebSocketDomainMessageEvent(
    val message: String
) : WebSocketMessageEvent() {

}