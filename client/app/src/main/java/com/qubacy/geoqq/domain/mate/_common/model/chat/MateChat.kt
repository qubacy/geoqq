package com.qubacy.geoqq.domain.mate._common.model.chat

import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain.mate._common.model.message.MateMessage
import com.qubacy.geoqq.domain.mate._common.model.message.toMateMessage

data class MateChat(
    val id: Long,
    val user: User,
    val newMessageCount: Int,
    val lastMessage: MateMessage?,
    val lastActionTime: Long
) {

}

fun DataMateChat.toMateChat(): MateChat {
    return MateChat(id, user.toUser(), newMessageCount, lastMessage?.toMateMessage(), lastActionTime)
}