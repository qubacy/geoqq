package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.Packet
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.header.EventHeader

class Event(
    header: EventHeader,
    payload: PacketPayload
) : Packet(header, payload) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Event) return false

        return (header == other.header && payload == other.payload)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}