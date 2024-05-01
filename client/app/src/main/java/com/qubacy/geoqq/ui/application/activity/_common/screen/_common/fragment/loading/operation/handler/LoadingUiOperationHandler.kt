package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.LoadingFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.operation.handler._common.UiOperationHandler

class LoadingUiOperationHandler(
    fragment: LoadingFragment
) : UiOperationHandler<LoadingFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        if (uiOperation !is SetLoadingStateUiOperation) return false

        fragment.adjustUiWithLoadingState(uiOperation.isLoading)

        return true
    }
}