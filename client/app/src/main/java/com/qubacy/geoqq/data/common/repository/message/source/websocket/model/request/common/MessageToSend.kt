package com.qubacy.geoqq.data.common.repository.message.source.websocket.model.request.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class MessageToSend(
    val text: String
) {

}