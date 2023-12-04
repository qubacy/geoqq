package com.qubacy.geoqq.data.common.repository.message.source.websocket.model.request

import com.qubacy.geoqq.data.common.repository.message.source.websocket.model.request.common.MessageToSend
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SendMessageRequestBody(
    @Json(name = "access-token") val accessToken: String,
    val message: MessageToSend
) {

}