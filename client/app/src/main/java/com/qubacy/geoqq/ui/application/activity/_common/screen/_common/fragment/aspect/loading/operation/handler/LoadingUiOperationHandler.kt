package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.LoadingFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler

class LoadingUiOperationHandler(
    fragment: LoadingFragment
) : UiOperationHandler<LoadingFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        if (uiOperation !is SetLoadingStateUiOperation) return false

        fragment.adjustUiWithLoadingState(uiOperation.isLoading)

        return true
    }
}