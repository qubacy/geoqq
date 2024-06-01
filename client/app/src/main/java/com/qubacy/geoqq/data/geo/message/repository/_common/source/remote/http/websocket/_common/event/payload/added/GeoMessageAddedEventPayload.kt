package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.message.MessageEventPayload
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GeoMessageAddedEventPayload(
    id: Long,
    text: String,
    time: Long,
    userId: Long
) : MessageEventPayload(id, text, time, userId) {

}