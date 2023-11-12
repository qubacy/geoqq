package com.qubacy.geoqq.domain.common.model

class Message(
    val id: Long,
    val sender: User,
    val text: String,
    val timestamp: Long
) {

}