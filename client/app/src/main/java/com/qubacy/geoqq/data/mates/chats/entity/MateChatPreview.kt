package com.qubacy.geoqq.data.mates.chats.entity

import android.net.Uri
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.mates.chat.entity.MateChat

class MateChatPreview(
    chatId: Long,
    avatar: Uri?,
    chatName: String,
    val lastMessage: Message
    ) : MateChat(chatId, avatar, chatName) {

}