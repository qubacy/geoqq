package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.callback

import com.squareup.moshi.JsonAdapter

interface ServerEventJsonAdapterCallback {
    fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>?
}