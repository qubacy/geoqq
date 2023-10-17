package com.qubacy.geoqq.data.mates.request.state

import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.state.State
import com.qubacy.geoqq.data.mates.request.entity.MateRequest

class MateRequestsState(
    val mateRequests: List<MateRequest> = listOf(),
    val users: List<User> = listOf(),
    newOperations: List<Operation>
) : State(newOperations) {

}