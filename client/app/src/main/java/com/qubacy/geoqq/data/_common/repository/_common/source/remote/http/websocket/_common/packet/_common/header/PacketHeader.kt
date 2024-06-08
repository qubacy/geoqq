package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.header

import kotlin.reflect.full.isSubclassOf

abstract class PacketHeader(
    val type: String
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || !other::class.isSubclassOf(PacketHeader::class))
            return false

        other as PacketHeader

        return (type == other.type)
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}