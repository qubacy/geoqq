package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._common.ActionJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.squareup.moshi.JsonWriter
import okio.Buffer
import javax.inject.Inject

class ActionJsonAdapterImpl @Inject constructor() : ActionJsonAdapter {
    override fun toJson(
        middlewares: List<ClientEventJsonMiddleware>,
        action: PackagedAction
    ): String {
        val buffer = Buffer()
        val jsonWriter = JsonWriter.of(buffer)

        jsonWriter.use {
            it.beginObject()

            it.name(ActionJsonAdapter.TYPE_PROP_NAME)
            it.value(action.type)

            applyMiddlewares(it, middlewares)

            it.name(ActionJsonAdapter.PAYLOAD_PROP_NAME)
            it.jsonValue(action.payload)

            it.endObject()
        }

        return buffer.readUtf8()
    }

    private fun applyMiddlewares(
        jsonWriter: JsonWriter,
        middlewares: List<ClientEventJsonMiddleware>
    ) {
        for (middleware in middlewares) middleware.process(jsonWriter)
    }
}