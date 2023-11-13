package com.qubacy.geoqq.data.mates.request.state

import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.common.State
import com.qubacy.geoqq.data.mates.request.entity.MateRequest

class MateRequestsState(
    val mateRequests: List<MateRequest> = listOf(),
    val users: List<User> = listOf(),
    newOperations: List<Operation>
) : State(newOperations) {

}