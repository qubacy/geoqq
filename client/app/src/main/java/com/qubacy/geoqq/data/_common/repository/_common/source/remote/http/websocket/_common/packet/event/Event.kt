package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.Packet
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.header.EventHeader

class Event(
    header: EventHeader,
    payload: PacketPayload
) : Packet(header, payload) {

}