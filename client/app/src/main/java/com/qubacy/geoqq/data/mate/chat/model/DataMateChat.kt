package com.qubacy.geoqq.data.mate.chat.model

class DataMateChat(
    val id: Long,
    val userId: Long,
    val newMessageCount: Int,
    val lastMessageId: Long // todo: it has to ref. to DataMessage obj.
) {

}