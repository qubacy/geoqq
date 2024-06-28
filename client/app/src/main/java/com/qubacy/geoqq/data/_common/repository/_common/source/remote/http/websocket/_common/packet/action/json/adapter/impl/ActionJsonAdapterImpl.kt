package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._common.ActionJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client._common.ActionJsonMiddleware
import com.squareup.moshi.JsonWriter
import okio.Buffer
import javax.inject.Inject
import kotlin.text.StringBuilder

class ActionJsonAdapterImpl @Inject constructor() : ActionJsonAdapter {
    companion object {
        const val TAG = "ActionJsonAdapterImpl";

        val REMOVABLE_CHARS = arrayOf('\\')
    }

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
        val firstPayloadMarkIndex = json.indexOf('\"', payloadStartIndex)
        val secondPayloadMarkIndex = json.lastIndexOf('\"') - 1

        rawJson.deleteCharAt(firstPayloadMarkIndex)
        rawJson.deleteCharAt(secondPayloadMarkIndex)

        preparePayloadJson(rawJson, firstPayloadMarkIndex, secondPayloadMarkIndex - 1);

        return rawJson.toString()
    }

    private fun preparePayloadJson(
        rawJson: StringBuilder,
        payloadStartIndex: Int,
        payloadEndIndex: Int
    ) {
        val removableCharIndexList = mutableListOf<Int>();

        for (index in payloadStartIndex..payloadEndIndex) {
            val char = rawJson[index]

            if (char in REMOVABLE_CHARS) removableCharIndexList.add(index)
        }

        for (removableCharIndex in removableCharIndexList.reversed()) {
            rawJson.deleteCharAt(removableCharIndex)
        }
    }
}