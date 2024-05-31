package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client._common.ActionJsonMiddleware

interface ActionJsonAdapter {
    companion object {
        const val TYPE_PROP_NAME = "action"
        const val PAYLOAD_PROP_NAME = "payload"
    }

    fun toJson(
        middlewares: List<ActionJsonMiddleware>,
        action: PackagedAction
    ): String
}