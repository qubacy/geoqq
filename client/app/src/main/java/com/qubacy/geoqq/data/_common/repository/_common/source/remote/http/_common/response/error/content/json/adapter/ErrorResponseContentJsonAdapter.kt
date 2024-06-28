package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class ErrorResponseContentJsonAdapter : JsonAdapter<ErrorResponseContent>() {
    companion object {
        const val TAG = "ErrorRespContJsonAdapter"

        const val ID_PROP_NAME = "id"
    }

    override fun fromJson(p0: JsonReader): ErrorResponseContent? {
        var errorId: Long? = null

        with(p0) {
            isLenient = true

            beginObject()

            while (hasNext()) {
                when (selectName(JsonReader.Options.of(ID_PROP_NAME))) {
                    0 -> {
                        errorId = p0.nextLong()
                    }
                    else -> {
                        skipName()
                        skipValue()
                    }
                }
            }

            endObject()
        }

        if (errorId == null) return null

        return ErrorResponseContent(errorId!!)
    }

    override fun toJson(p0: JsonWriter, p1: ErrorResponseContent?) {

    }
}