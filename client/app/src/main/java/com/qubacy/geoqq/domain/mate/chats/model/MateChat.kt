package com.qubacy.geoqq.domain.mate.chats.model

import android.net.Uri
import com.qubacy.geoqq.domain.common.model.Message

class MateChat(
    val chatId: Long,
    val avatarUri: Uri,
    val chatName: String,
    val lastMessage: Message
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MateChat) return false
        if (this === other) return true

        return chatId == other.chatId
    }
}
