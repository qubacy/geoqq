package com.qubacy.geoqq.data.common.message.repository.source.network.model.response.common

import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MessageNetworkModel(
    val id: Long,
    @Json(name = "user-id") val userId: Long,
    val text: String,
    val time: Long
) {

}

fun MessageNetworkModel.toDataMessage(): DataMessage {
    return DataMessage(id, userId, text, time)
}