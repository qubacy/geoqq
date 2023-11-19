package com.qubacy.geoqq.domain.mate.request.state

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.common.State
import com.qubacy.geoqq.domain.mate.request.model.MateRequest

class MateRequestsState(
    val mateRequests: List<MateRequest> = listOf(),
    val users: List<User> = listOf(),
    newOperations: List<Operation>
) : State(newOperations) {

}