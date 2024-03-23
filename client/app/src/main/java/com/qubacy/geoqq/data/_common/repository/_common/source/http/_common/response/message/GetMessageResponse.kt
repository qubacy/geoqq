package com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMessageResponse(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = USER_ID_PROP_NAME) val userId: Long,
    @Json(name = TEXT_PROP_NAME) val text: String,
    @Json(name = TIME_PROP_NAME) val time: Long
) {
    companion object {
        const val ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user-id"
        const val TEXT_PROP_NAME = "text"
        const val TIME_PROP_NAME = "time"
    }
}

fun GetMessageResponse.toDataMessage(): DataMessage {
    return DataMessage(id, userId, text, time)
}