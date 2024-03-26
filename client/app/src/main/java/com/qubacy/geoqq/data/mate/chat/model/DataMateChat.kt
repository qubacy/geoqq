package com.qubacy.geoqq.data.mate.chat.model

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.model.message.toMateMessageEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.toDataMessage

data class DataMateChat(
    val id: Long,
    val userId: Long,
    val newMessageCount: Int,
    val lastMessage: DataMessage?
) {

}

fun DataMateChat.toMateChatLastMessageEntityPair(): Pair<MateChatEntity, MateMessageEntity?> {
    val mateChatEntity = MateChatEntity(id, userId, newMessageCount, lastMessage?.id)

    return Pair(mateChatEntity, lastMessage?.toMateMessageEntity(id))
}

fun Map.Entry<MateChatEntity, MateMessageEntity?>.toDataMateChat(): DataMateChat {
    return DataMateChat(
        key.id,
        key.userId,
        key.newMessageCount,
        value?.toDataMessage()
    )
}

//fun DataMateChat.toMateChatWithLastMessageProjection(): MateChatWithLastMessageProjection {
//    val mateChatEntity = MateChatEntity(id, userId, newMessageCount, lastMessage?.id)
//
//    return MateChatWithLastMessageProjection(
//        mateChatEntity, lastMessage?.toMateMessageEntity(id)
//    )
//}

//fun MateChatWithLastMessageProjection.toDataMateChat(): DataMateChat {
//    return DataMateChat(
//        mateChatEntity.id,
//        mateChatEntity.userId,
//        mateChatEntity.newMessageCount,
//        lastMessage?.toDataMessage()
//    )
//}

fun GetChatResponse.toDataMateChat(): DataMateChat {
    val lastDataMessage = lastMessage?.toDataMessage()

    return DataMateChat(id, userId, newMessageCount, lastDataMessage)
}