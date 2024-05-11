package com.qubacy.geoqq.data.mate.message.model

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.model.DataUser

fun MateMessageEntity.toDataMessage(user: DataUser): DataMessage {
    return DataMessage(id, user, text, timeInSec * 1000)
}