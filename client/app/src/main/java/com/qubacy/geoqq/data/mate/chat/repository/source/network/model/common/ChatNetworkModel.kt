package com.qubacy.geoqq.data.mate.chat.repository.source.network.model.common

import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.common.MessageNetworkModel
import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.common.toDataMessage
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ChatNetworkModel(
    val id: Long,
    @Json(name = "user-id") val userId: Long,
    @Json(name = "new-message-count") val newMessageCount: Int,
    @Json(name = "last-message") val lastMessage: MessageNetworkModel?
) {

}

fun ChatNetworkModel.toDataMateChat(): DataMateChat {
    return DataMateChat(id, userId, newMessageCount, lastMessage?.toDataMessage())
}

@JsonClass(generateAdapter = true)
class ChatUserInfo(
    val id: Long,
    val username: String,
    val isMate: Boolean,
    @Json(name = "avatar-id") val avatarId: Long
)

@JsonClass(generateAdapter = true)
class ChatLastMessage(

)