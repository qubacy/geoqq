package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.factory._test.mock

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class ViewModelMockContext<UiStateType : BaseUiState>(
    var uiState: UiStateType,
    var uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    var retrieveErrorResult: Error? = null
) {
    open fun reset() {
        retrieveErrorResult = null
    }
}