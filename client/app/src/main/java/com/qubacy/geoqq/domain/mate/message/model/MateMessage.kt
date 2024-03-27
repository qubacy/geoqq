package com.qubacy.geoqq.domain.mate.message.model

import com.qubacy.geoqq.domain._common.model.user.User

data class MateMessage(
    val id: Long,
    val user: User,
    val text: String,
    val time: Long
) {
}