package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler.error

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.StatefulFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler

class ErrorUiOperationHandler(
    fragment: StatefulFragment<*, *, *>
) : UiOperationHandler<StatefulFragment<*, *, *>>(fragment) {
    override fun handleUiOperation(
        uiOperation: UiOperation
    ): Boolean {
        if (uiOperation !is ErrorUiOperation) return false

        fragment.onErrorOccurred(uiOperation.error)

        return true
    }
}