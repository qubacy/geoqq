package com.qubacy.geoqq._common.util.json.adapter.extension

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader

fun JsonAdapter<*>.skipObject(reader: JsonReader) {
    reader.beginObject()

    while (reader.hasNext()) {
        reader.skipName()
        reader.skipValue()
    }

    reader.endObject()
}