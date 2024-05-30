package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.callback

import com.squareup.moshi.JsonAdapter

interface EventJsonAdapterCallback {
    fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>?
}