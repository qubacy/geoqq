package com.qubacy.geoqq.domain.mate._common.model.request

import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.model.user.toUser

data class MateRequest(
    val id: Long,
    val user: User
) {

}

fun DataMateRequest.toMateRequest(): MateRequest {
    return MateRequest(id, user.toUser())
}