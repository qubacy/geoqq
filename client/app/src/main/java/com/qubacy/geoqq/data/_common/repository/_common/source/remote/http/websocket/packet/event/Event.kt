package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet._common.Packet
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event.header.EventHeader

class Event(
    header: EventHeader,
    payload: PacketPayload
) : Packet(header, payload) {

}