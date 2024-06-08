package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import kotlin.reflect.full.isSubclassOf

class ErrorEventPayload(
    val code: Long,
    val error: ErrorResponseContent
) : PacketPayload {
    override fun equals(other: Any?): Boolean {
        if (other == null || !other::class.isSubclassOf(ErrorEventPayload::class))
            return false

        other as ErrorEventPayload

        return (code == other.code && error == other.error)
    }

    override fun hashCode(): Int {
        var result = code.hashCode()

        result = 31 * result + error.hashCode()

        return result
    }
}