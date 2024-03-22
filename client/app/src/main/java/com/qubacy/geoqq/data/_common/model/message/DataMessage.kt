package com.qubacy.geoqq.data._common.model.message

import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity

class DataMessage(
    val id: Long = DEFAULT_ID,
    val userId: Long = DEFAULT_ID,
    val text: String,
    val time: Long
) {
    companion object {
        const val DEFAULT_ID = -1L
    }
}

fun DataMessage.toMateMessageEntity(chatId: Long): MateMessageEntity {
    return MateMessageEntity(id, chatId, userId, text, time / 1000)
}