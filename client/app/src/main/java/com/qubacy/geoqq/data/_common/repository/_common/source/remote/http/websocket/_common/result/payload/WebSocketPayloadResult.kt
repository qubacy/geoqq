package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult

class WebSocketPayloadResult(
    val type: String,
    val payload: PacketPayload
) : WebSocketResult {

}