package com.qubacy.geoqq.domain.common.model.message

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.common.MessageBase

class Message(
    val id: Long,
    val sender: User,
    text: String,
    timestamp: Long
) : MessageBase(text, timestamp) {

}