package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.header

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.header.EventHeader

class ClientEventHeader(
    type: String,
    val accessToken: String
) : EventHeader(type) {

}