package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.Event
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.payload.EventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.header.ServerEventHeader

class ServerEvent(
    header: ServerEventHeader,
    payload: EventPayload
) : Event(header, payload) {

}