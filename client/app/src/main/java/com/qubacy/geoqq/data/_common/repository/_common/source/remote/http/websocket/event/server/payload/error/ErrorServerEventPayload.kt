package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.payload.error

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.payload.EventPayload

class ErrorServerEventPayload(
    val code: Long,
    val error: ErrorResponseContent
) : EventPayload {

}