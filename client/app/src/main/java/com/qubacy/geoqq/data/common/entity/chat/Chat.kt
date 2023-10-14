package com.qubacy.geoqq.data.common.entity.chat

import android.net.Uri
import com.qubacy.geoqq.data.common.entity.chat.message.Message

data class Chat(
    val chatId: Long,
    val avatar: Uri? = null,
    val chatName: String,
    val lastMessage: Message
)
