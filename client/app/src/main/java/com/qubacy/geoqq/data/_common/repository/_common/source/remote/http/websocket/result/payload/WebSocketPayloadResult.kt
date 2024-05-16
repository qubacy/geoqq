package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.payload

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.payload.EventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result._common.WebSocketResult

class WebSocketPayloadResult(
    val type: String,
    val payload: EventPayload
) : WebSocketResult {

}