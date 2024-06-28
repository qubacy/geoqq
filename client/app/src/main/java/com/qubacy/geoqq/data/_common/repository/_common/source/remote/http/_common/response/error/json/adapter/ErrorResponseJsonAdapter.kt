package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter.ErrorResponseContentJsonAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class ErrorResponseJsonAdapter(
    private val mErrorResponseContentJsonAdapter: ErrorResponseContentJsonAdapter
) : JsonAdapter<ErrorResponse>() {
    companion object {
        const val TAG = "ErrorResponseJsonAdapter"
    }

    override fun fromJson(p0: JsonReader): ErrorResponse? {
        var errorResponseContent: ErrorResponseContent? = null

        with(p0) {
            isLenient = true

            beginObject()
            skipName()

            errorResponseContent = mErrorResponseContentJsonAdapter.fromJson(p0);

            endObject()
        }

        if (errorResponseContent == null) return null

        return ErrorResponse(errorResponseContent!!)
    }

    override fun toJson(p0: JsonWriter, p1: ErrorResponse?) {

    }
}