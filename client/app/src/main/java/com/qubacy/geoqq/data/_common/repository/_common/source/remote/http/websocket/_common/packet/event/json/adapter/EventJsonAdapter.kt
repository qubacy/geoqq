package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.Event
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.header.EventHeader
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.callback.EventJsonAdapterCallback
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import javax.inject.Inject

class EventJsonAdapter @Inject constructor() : JsonAdapter<Event>() {
    companion object {
        const val TYPE_PROP_NAME = "event"
        const val PAYLOAD_PROP_NAME = "payload"
    }

    private lateinit var mCallback: EventJsonAdapterCallback

    fun setCallback(callback: EventJsonAdapterCallback) {
        mCallback = callback
    }

    override fun fromJson(p0: JsonReader): Event? {
        var type: String? = null
        var payload: PacketPayload? = null

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

                        if (payloadAdapter == null) { skipObject(p0) }
                        else { payload = payloadAdapter.fromJson(p0) as PacketPayload }
                    }
                    else -> {
                        skipName()
                        skipValue()
                    }
                }
            }

            endObject()
        }

        if (payload == null) return null

        val serverEventHeader = EventHeader(type!!)
        val serverEvent = Event(serverEventHeader, payload!!)

        return serverEvent
    }

    private fun skipObject(reader: JsonReader) {
        reader.beginObject()

        while (reader.hasNext()) {
            reader.skipName()
            reader.skipValue()
        }

        reader.endObject()
    }

    override fun toJson(p0: JsonWriter, p1: Event?) {

    }
}