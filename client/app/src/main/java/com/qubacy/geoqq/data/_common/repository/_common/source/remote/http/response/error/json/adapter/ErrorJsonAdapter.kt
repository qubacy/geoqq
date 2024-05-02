package com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponseContent
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class ErrorJsonAdapter : JsonAdapter<ErrorResponse>() {
    override fun fromJson(p0: JsonReader): ErrorResponse? {
        p0.beginObject() // {
        p0.skipName()
        p0.beginObject()
        p0.skipName()

        val errorId = p0.nextLong()
        val errorContent = ErrorResponseContent(errorId)

        return ErrorResponse(errorContent)
    }

    override fun toJson(p0: JsonWriter, p1: ErrorResponse?) {
        TODO("Not yet implemented")
    }
}