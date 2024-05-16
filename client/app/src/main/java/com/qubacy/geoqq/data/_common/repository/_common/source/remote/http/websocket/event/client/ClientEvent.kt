package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.Event
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.payload.EventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.header.ClientEventHeader

class ClientEvent(
    header: ClientEventHeader,
    payload: EventPayload
) : Event(header, payload) {

}