package com.qubacy.geoqq.data.mate.chat.model

import com.qubacy.geoqq.data.common.model.message.DataMessage

class DataMateChat(
    val id: Long,
    val userId: Long,
    val newMessageCount: Int,
    val lastMessage: DataMessage?
) {

}