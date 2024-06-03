package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GeoMessageActionPayload(
    @Json(name = TEXT_PROP_NAME) val text: String,
    @Json(name = LONGITUDE_PROP_NAME) val longitude: Float,
    @Json(name = LATITUDE_PROP_NAME) val latitude: Float
) {
    companion object {
        const val TEXT_PROP_NAME = "text"
        const val LONGITUDE_PROP_NAME = "longitude"
        const val LATITUDE_PROP_NAME = "latitude"
    }
}