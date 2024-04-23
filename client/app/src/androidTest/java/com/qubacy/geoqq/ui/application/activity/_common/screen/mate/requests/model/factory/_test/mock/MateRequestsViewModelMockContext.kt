package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory._test.mock

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class MateRequestsViewModelMockContext(
    uiState: MateRequestsUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var getUserProfileWithMateRequestId: UserPresentation? = null,
    var getUserProfileWithMateRequestIdCallFlag: Boolean = false,
    var getNextRequestChunkCallFlag: Boolean = false,
    var answerRequestCallFlag: Boolean = false,
    var resetRequestsCallFlag: Boolean = false
) : ViewModelMockContext<MateRequestsUiState>(uiState, uiOperationFlow, retrieveErrorResult) {
    override fun reset() {
        super.reset()

        uiState = MateRequestsUiState()

        getUserProfileWithMateRequestId = null

        getUserProfileWithMateRequestIdCallFlag = false
        getNextRequestChunkCallFlag = false
        answerRequestCallFlag = false
        resetRequestsCallFlag = false
    }
}