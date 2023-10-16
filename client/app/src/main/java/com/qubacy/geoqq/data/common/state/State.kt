package com.qubacy.geoqq.data.common.state

import com.qubacy.geoqq.data.common.operation.Operation

abstract class State(
    val newOperations: List<Operation> = listOf()
) {

}