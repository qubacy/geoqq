package com.qubacy.geoqq.domain.common.state.common

import com.qubacy.geoqq.domain.common.operation.common.Operation

abstract class State(
    val newOperations: List<Operation> = listOf()
) {

}