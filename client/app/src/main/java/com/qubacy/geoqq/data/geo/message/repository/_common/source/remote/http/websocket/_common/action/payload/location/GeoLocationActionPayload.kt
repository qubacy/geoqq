package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.location

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GeoLocationActionPayload(
    @Json(name = LONGITUDE_PROP_NAME) val longitude: Float,
    @Json(name = LATITUDE_PROP_NAME) val latitude: Float,
    @Json(name = RADIUS_PROP_NAME) val radius: Int
) {
    companion object {
        const val LONGITUDE_PROP_NAME = "longitude"
        const val LATITUDE_PROP_NAME = "latitude"
        const val RADIUS_PROP_NAME = "radius"
    }
}