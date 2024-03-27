package com.qubacy.geoqq.data._common.model.message

import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message.GetMessageResponse
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.model.DataUser

data class DataMessage(
    val id: Long = DEFAULT_ID,
    val user: DataUser,
    val text: String,
    val time: Long
) {
    companion object {
        const val DEFAULT_ID = -1L
    }
}

fun DataMessage.toMateMessageEntity(chatId: Long): MateMessageEntity {
    return MateMessageEntity(id, chatId, user.id, text, time / 1000)
}

fun GetMessageResponse.toDataMessage(user: DataUser): DataMessage {
    return DataMessage(id, user, text, time)
}