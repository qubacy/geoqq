package com.qubacy.geoqq.ui.common.fragment.common.base.model.state

import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation

abstract class OperationUiState(
    val newUiOperations: List<UiOperation>
) : UiState() {

}