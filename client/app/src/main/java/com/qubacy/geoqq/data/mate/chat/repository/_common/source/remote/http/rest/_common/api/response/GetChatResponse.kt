package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response

import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessageResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetChatResponse(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = USER_ID_PROP_NAME) val userId: Long,
    @Json(name = NEW_MESSAGE_COUNT_PROP_NAME) val newMessageCount: Int,
    @Json(name = LAST_MESSAGE_PROP_NAME) val lastMessage: GetMessageResponse?
) {
    companion object {
        const val ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user-id"
        const val NEW_MESSAGE_COUNT_PROP_NAME = "new-message-count"
        const val LAST_MESSAGE_PROP_NAME = "last-message"
    }
}