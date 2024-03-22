package com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMessageResponse(
    val id: Long,
    @Json(name = "user-id") val userId: Long,
    val text: String,
    val time: Long
) {

}

fun GetMessageResponse.toDataMessage(): DataMessage {
    return DataMessage(id, userId, text, time)
}