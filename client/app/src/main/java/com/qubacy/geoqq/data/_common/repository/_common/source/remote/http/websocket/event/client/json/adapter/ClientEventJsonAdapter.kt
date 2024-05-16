package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.squareup.moshi.JsonWriter
import okio.Buffer

class ClientEventJsonAdapter {
    companion object {
        const val TYPE_PROP_NAME = "route"
        const val PAYLOAD_PROP_NAME = "payload"
    }

    fun toJson(
        middlewares: List<ClientEventJsonMiddleware>,
        type: String,
        payload: String
    ): String {
        val buffer = Buffer()
        val jsonWriter = JsonWriter.of(buffer)

        jsonWriter.use {
            it.beginObject()

            it.name(TYPE_PROP_NAME)
            it.value(type)

            applyMiddlewares(it, middlewares)

            it.name(PAYLOAD_PROP_NAME)
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