package com.qubacy.geoqq.data.common.message.repository.source.network.model.request.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MessageToSend(
    val text: String
) {

}