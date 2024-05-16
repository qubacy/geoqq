package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.header.EventHeader
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.payload.EventPayload

abstract class Event(
    val header: EventHeader,
    val payload: EventPayload
) {

}