package com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter

import android.util.Log
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponseContent
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class ErrorJsonAdapter : JsonAdapter<ErrorResponse>() {
    companion object {
        const val TAG = "ErrorJsonAdapter"
    }

    override fun fromJson(p0: JsonReader): ErrorResponse {
        var errorId: Long? = null

        with(p0) {
            isLenient = true

            beginObject()
            skipName()
            beginObject()

            while (hasNext()) {
                when (selectName(JsonReader.Options.of("id"))) {
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
            endObject()
        }

//        Log.d(TAG, "fromJson(): errorId = $errorId;")

        val errorContent = ErrorResponseContent(errorId!!)

        return ErrorResponse(errorContent)
    }

    override fun toJson(p0: JsonWriter, p1: ErrorResponse?) {
        TODO("Not yet implemented")
    }
}