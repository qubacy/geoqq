package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event.payload.success.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event.payload.success.SuccessEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class SuccessEventPayloadJsonAdapter : JsonAdapter<SuccessEventPayload>() {
    override fun fromJson(p0: JsonReader): SuccessEventPayload {
        // todo: nothing to do?

        return SuccessEventPayload()
    }

    override fun toJson(p0: JsonWriter, p1: SuccessEventPayload?) {

    }
}