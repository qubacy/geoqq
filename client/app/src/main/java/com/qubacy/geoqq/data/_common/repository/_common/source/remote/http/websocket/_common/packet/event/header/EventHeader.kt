package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.header

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.header.PacketHeader

class EventHeader(
    type: String
) : PacketHeader(type) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is EventHeader
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}