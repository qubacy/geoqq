package com.qubacy.geoqq.domain.mate.chats.model

import android.net.Uri
import com.qubacy.geoqq.domain.common.model.message.common.MessageBase

class MateChat(
    val chatId: Long,
    val interlocutorUserId: Long,
    val avatarUri: Uri,
    val lastMessage: MessageBase?,
    val newMessageCount: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MateChat) return false
        if (this === other) return true

        return chatId == other.chatId
    }
}
