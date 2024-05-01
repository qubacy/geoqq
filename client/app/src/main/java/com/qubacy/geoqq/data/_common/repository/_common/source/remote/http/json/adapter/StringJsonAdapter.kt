package com.qubacy.geoqq.data._common.repository._common.source.remote.http.json.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class StringJsonAdapter : JsonAdapter<String>() {
    override fun fromJson(p0: JsonReader): String? {
        return unescape(p0.nextString())
    }

    override fun toJson(p0: JsonWriter, p1: String?) {
        p0.value(p1)
    }

    fun unescape(str: String) : String {
        str.replace("\\s+", " ")
        str.replace("\\n","")
        str.replace("\\u003d","=")
        str.replace("\\u003c","<")
        str.replace("\\u003e",">")
        str.replace("\\u0027","'")
        str.replace("\\","\"")
        str.replace("\\t", "    ")

        return str
    }
}