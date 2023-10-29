package com.qubacy.geoqq.ui.common.fragment.common.base.model.state

import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation

abstract class OperationUiState(
    newUiOperations: List<UiOperation> = listOf()
) : UiState() {
    private val mNewUiOperations = newUiOperations.toMutableList()

    fun takeUiOperation(): UiOperation? {
        if (mNewUiOperations.isEmpty()) return null

        return mNewUiOperations.removeFirst()
    }

    fun uiOperationCount(): Int {
        return mNewUiOperations.size
    }
}