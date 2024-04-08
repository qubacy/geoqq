package com.qubacy.geoqq.domain.mate.chat.model

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.model.user.toUser

data class MateMessage(
    val id: Long,
    val user: User,
    val text: String,
    val time: Long
) {

}

fun DataMessage.toMateMessage(): MateMessage {
    return MateMessage(id, user.toUser(), text, time)
}