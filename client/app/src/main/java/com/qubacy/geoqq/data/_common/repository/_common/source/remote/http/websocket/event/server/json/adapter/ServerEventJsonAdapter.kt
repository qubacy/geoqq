package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.payload.EventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.ServerEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.header.ServerEventHeader
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.callback.ServerEventJsonAdapterCallback
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import javax.inject.Inject

class ServerEventJsonAdapter @Inject constructor(
    private val mCallback: ServerEventJsonAdapterCallback
) : JsonAdapter<ServerEvent>() {
    companion object {
        const val TYPE_PROP_NAME = "event"
        const val PAYLOAD_PROP_NAME = "payload"
    }

    override fun fromJson(p0: JsonReader): ServerEvent? {
        var type: String? = null
        var payload: EventPayload? = null

        with(p0) {
            isLenient = true

            beginObject()

            while (hasNext()) {
                when (selectName(JsonReader.Options.of(TYPE_PROP_NAME, PAYLOAD_PROP_NAME))) {
                    0 -> {
                        type = p0.nextString()
                    }
                    1 -> {
                        val payloadAdapter = mCallback.getEventPayloadJsonAdapterByType(type!!)

                        if(payloadAdapter == null) return null

                        payload = payloadAdapter.fromJson(p0) as EventPayload
                    }
                    else -> {
                        skipName()
                        skipValue()
                    }
                }
            }

            endObject()
        }

        val serverEventHeader = ServerEventHeader(type!!)
        val serverEvent = ServerEvent(serverEventHeader, payload!!)

        return serverEvent
    }

    override fun toJson(p0: JsonWriter, p1: ServerEvent?) {
        TODO("Not yet implemented")
    }
}