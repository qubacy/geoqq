package com.qubacy.geoqq.data.common.entity.chat.message

data class Message(
    val messageId: Long,
    val userId: Long,
    val text: String,
    val timestamp: Long
)