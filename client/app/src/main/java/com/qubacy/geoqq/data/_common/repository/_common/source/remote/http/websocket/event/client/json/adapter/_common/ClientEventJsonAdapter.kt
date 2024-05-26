package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client._common.ClientEventJsonMiddleware

interface ClientEventJsonAdapter {
    companion object {
        const val TYPE_PROP_NAME = "action"
        const val PAYLOAD_PROP_NAME = "payload"
    }

    fun toJson(
        middlewares: List<ClientEventJsonMiddleware>,
        type: String,
        payload: String
    ): String
}