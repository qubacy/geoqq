package com.qubacy.geoqq.data.mates.chat.entity

import android.net.Uri
import com.qubacy.geoqq.data.common.entity.chat.Chat

open class MateChat(
    val chatId: Long,
    val avatar: Uri? = null,
    val chatName: String
) : Chat() {
    override fun equals(other: Any?): Boolean {
        if (other !is MateChat) return false
        if (this === other) return true

        return chatId == other.chatId
    }
}