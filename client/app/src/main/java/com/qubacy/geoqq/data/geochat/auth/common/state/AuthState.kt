package com.qubacy.geoqq.data.geochat.auth.common.state

import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.state.State

class AuthState(
    val isAuthorized: Boolean = false,
    newOperations: List<Operation> = listOf()
) : State(newOperations) {

}