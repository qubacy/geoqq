package com.qubacy.geoqq.data._common.model.message

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.message.MessageEventPayload
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessageResponse
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
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
    return MateMessageEntity(id, chatId, user.id, text, time)
}

fun GetMessageResponse.toDataMessage(user: DataUser): DataMessage {
    return DataMessage(id, user, text, time)
}

fun MessageEventPayload.toDataMessage(user: DataUser): DataMessage {
    return DataMessage(id, user, text, time)
}