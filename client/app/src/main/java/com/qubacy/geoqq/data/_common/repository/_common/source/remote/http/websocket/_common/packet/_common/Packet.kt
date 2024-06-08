package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.header.PacketHeader
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import kotlin.reflect.full.isSubclassOf

abstract class Packet(
    val header: PacketHeader,
    val payload: PacketPayload
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || !other::class.isSubclassOf(Packet::class)) return false

        other as Packet

        return (header == other.header && payload == other.payload)
    }
}