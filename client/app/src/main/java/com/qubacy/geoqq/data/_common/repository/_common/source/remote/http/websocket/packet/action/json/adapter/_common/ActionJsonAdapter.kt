package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.action.json.adapter._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client._common.ClientEventJsonMiddleware

interface ActionJsonAdapter {
    companion object {
        const val TYPE_PROP_NAME = "action"
        const val PAYLOAD_PROP_NAME = "payload"
    }

    fun toJson(
        middlewares: List<ClientEventJsonMiddleware>,
        action: PackagedAction
    ): String
}