package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event.payload.error

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet._common.payload.PacketPayload

class ErrorEventPayload(
    val code: Long,
    val error: ErrorResponseContent
) : PacketPayload {

}