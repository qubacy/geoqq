package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter.ErrorResponseContentJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.ErrorEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class ErrorEventPayloadJsonAdapter(
    private val mErrorResponseContentJsonAdapter: ErrorResponseContentJsonAdapter
) : JsonAdapter<ErrorEventPayload>() {
    companion object {
        const val CODE_PROP_NAME = "code"
        const val ERROR_PROP_NAME = "error"
    }

    override fun fromJson(p0: JsonReader): ErrorEventPayload? {
        var code: Long? = null
        lateinit var error: ErrorResponseContent

        with(p0) {
            isLenient = true

            beginObject()

            while (hasNext()) {
                when (selectName(JsonReader.Options.of(CODE_PROP_NAME, ERROR_PROP_NAME))) {
                    0 -> {
                        code = p0.nextLong()
                    }
                    1 -> {
                        error = mErrorResponseContentJsonAdapter.fromJson(p0) ?: return null
                    }
                    else -> {
                        skipName()
                        skipValue()
                    }
                }
            }

            endObject()
        }

        if (code == null) return null

        return ErrorEventPayload(code!!, error)
    }

    override fun toJson(p0: JsonWriter, p1: ErrorEventPayload?) {

    }
}