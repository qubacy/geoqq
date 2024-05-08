package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.factory._test.mock

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class BusinessViewModelMockContext<
    UiStateType : BusinessUiState
>(
    uiState: UiStateType,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    val backendResponded: Boolean = true,
    var setBackendRespondedCallFlag: Boolean = false
) : ViewModelMockContext<UiStateType>(
    uiState, uiOperationFlow, retrieveErrorResult
) {
    override fun reset() {
        super.reset()

        setBackendRespondedCallFlag = false
    }
}