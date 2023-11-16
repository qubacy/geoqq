package com.qubacy.geoqq.domain.common.operation.chat

import com.qubacy.geoqq.domain.common.operation.common.Operation

class SetUserDetailsOperation(
    val userId: Long,
    val isUpdated: Boolean
) : Operation() {

}