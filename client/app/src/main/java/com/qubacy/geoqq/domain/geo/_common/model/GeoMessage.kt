package com.qubacy.geoqq.domain.geo._common.model

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.model.user.toUser

data class GeoMessage(
    val id: Long,
    val user: User,
    val text: String,
    val time: Long
) {

}

fun DataMessage.toGeoMessage(): GeoMessage {
    return GeoMessage(id, user.toUser(), text, time)
}