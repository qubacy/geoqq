package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter._common.ClientEventJsonAdapter
import com.squareup.moshi.JsonWriter
import okio.Buffer

class ClientEventJsonAdapterImpl : ClientEventJsonAdapter {
    override fun toJson(
        middlewares: List<ClientEventJsonMiddleware>,
        type: String,
        payload: String
    ): String {
        val buffer = Buffer()
        val jsonWriter = JsonWriter.of(buffer)

        jsonWriter.use {
            it.beginObject()

            it.name(ClientEventJsonAdapter.TYPE_PROP_NAME)
            it.value(type)

            applyMiddlewares(it, middlewares)

            it.name(ClientEventJsonAdapter.PAYLOAD_PROP_NAME)
            it.jsonValue(payload)

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