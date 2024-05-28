package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet._common.header.PacketHeader
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet._common.payload.PacketPayload

abstract class Packet(
    val header: PacketHeader,
    val payload: PacketPayload
) {

}