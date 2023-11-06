package com.qubacy.geoqq.data.mate.chat.repository.source.network.model.common

import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// TODO: DISCUSSING THE STRUCTS..
@JsonClass(generateAdapter = true)
class ChatNetworkModel(
    val id: Long,
    val user: ChatUserInfo,
    @Json(name = "new-message-count") val newMessageCount: Int,
    @Json(name = "last-message") val lastMessage: ChatLastMessage
) {

}

fun ChatNetworkModel.toDataMateChat(): DataMateChat {
    return DataMateChat(id, 0, newMessageCount, 0) // todo: to fix.
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