package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client._common

import com.squareup.moshi.JsonWriter

interface ClientEventJsonMiddleware {
    fun process(jsonWriter: JsonWriter)
}