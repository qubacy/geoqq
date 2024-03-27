package com.qubacy.geoqq.data.mate.chat.model

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.model.message.toMateMessageEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.model.DataUser

data class DataMateChat(
    val id: Long,
    val user: DataUser,
    val newMessageCount: Int,
    val lastMessage: DataMessage?
) {

}

fun DataMateChat.toMateChatLastMessageEntityPair(): Pair<MateChatEntity, MateMessageEntity?> {
    val mateChatEntity = MateChatEntity(id, user.id, newMessageCount, lastMessage?.id)

    return Pair(mateChatEntity, lastMessage?.toMateMessageEntity(id))
}

fun Map.Entry<MateChatEntity, MateMessageEntity?>.toDataMateChat(
    user: DataUser,
    lastMessageUser: DataUser?
): DataMateChat {
    return DataMateChat(
        key.id,
        user,
        key.newMessageCount,
        value?.toDataMessage(lastMessageUser!!)
    )
}

fun GetChatResponse.toDataMateChat(user: DataUser, lastMessageUser: DataUser?): DataMateChat {
    val lastDataMessage = lastMessage?.toDataMessage(lastMessageUser!!)

    return DataMateChat(id, user, newMessageCount, lastDataMessage)
}