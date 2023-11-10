package com.qubacy.geoqq.data.common.message.repository.source.network.model.request

import com.qubacy.geoqq.data.common.message.repository.source.network.model.request.common.MessageToSend
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SendMessageRequestBody(
    @Json(name = "access-token") val accessToken: String,
    val message: MessageToSend
) {

}