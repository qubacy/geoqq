package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.payload

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result._common.WebSocketResult

class WebSocketPayloadResult(
    val type: String,
    val payload: PacketPayload
) : WebSocketResult {

}