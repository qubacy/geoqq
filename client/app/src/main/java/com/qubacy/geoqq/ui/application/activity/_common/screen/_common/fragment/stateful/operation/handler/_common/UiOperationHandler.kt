package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.operation.handler._common

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation

abstract class UiOperationHandler<FragmentType>(
    val fragment: FragmentType
) {
    abstract fun handleUiOperation(uiOperation: UiOperation): Boolean
}