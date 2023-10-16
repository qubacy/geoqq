package com.qubacy.geoqq.ui.common.fragment.common.model.state

import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation

abstract class OperationUiState(
    val newUiOperations: List<UiOperation>
) : UiState() {

}