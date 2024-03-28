package com.qubacy.geoqq.domain.mate.chats.model

import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain.mate.message.model.MateMessage
import com.qubacy.geoqq.domain.mate.message.model.toMateMessage

data class MateChat(
    val id: Long,
    val user: User,
    val newMessageCount: Int,
    val lastMessage: MateMessage?
) {

}

fun DataMateChat.toMateChat(): MateChat {
    return MateChat(id, user.toUser(), newMessageCount, lastMessage?.toMateMessage())
}