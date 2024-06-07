package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._common.ActionJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client._common.ActionJsonMiddleware
import com.squareup.moshi.JsonWriter
import okio.Buffer
import java.lang.StringBuilder
import javax.inject.Inject

class ActionJsonAdapterImpl @Inject constructor() : ActionJsonAdapter {
    override fun toJson(
        middlewares: List<ActionJsonMiddleware>,
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

        val json = buffer.readUtf8()

        return prepareJson(json)
    }

    private fun applyMiddlewares(
        jsonWriter: JsonWriter,
        middlewares: List<ActionJsonMiddleware>
    ) {
        for (middleware in middlewares) middleware.process(jsonWriter)
    }

    private fun prepareJson(json: String): String {
        val rawJson = StringBuilder(json)

        val payloadStartIndex = json.indexOf(ActionJsonAdapter.PAYLOAD_PROP_NAME) +
            ActionJsonAdapter.PAYLOAD_PROP_NAME.length + 1
        val firstPayloadMark = json.indexOf('\"', payloadStartIndex)
        val secondPayloadMark = json.lastIndexOf('\"') - 1

        rawJson.deleteCharAt(firstPayloadMark)
        rawJson.deleteCharAt(secondPayloadMark)

        return rawJson.toString()
    }
}