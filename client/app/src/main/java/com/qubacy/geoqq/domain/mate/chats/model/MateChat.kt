package com.qubacy.geoqq.domain.mate.chats.model

import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.mate.message.model.MateMessage

data class MateChat(
    val id: Long,
    val user: User,
    val newMessageCount: Int,
    val lastMessage: MateMessage?
) {

}