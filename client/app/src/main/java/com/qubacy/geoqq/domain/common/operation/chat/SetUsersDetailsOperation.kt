package com.qubacy.geoqq.domain.common.operation.chat

import com.qubacy.geoqq.domain.common.operation.common.Operation

class SetUsersDetailsOperation(
    val usersIds: List<Long>,
    val areUpdated: Boolean
) : Operation() {

}