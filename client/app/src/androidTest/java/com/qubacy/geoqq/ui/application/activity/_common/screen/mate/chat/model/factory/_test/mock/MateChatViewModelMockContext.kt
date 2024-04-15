package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory._test.mock

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class MateChatViewModelMockContext(
    uiState: MateChatUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var setChatContextCallFlag: Boolean = false,
    var getNextMessageChunkCallFlag: Boolean = false,
    var getInterlocutorProfileCallFlag: Boolean = false,
    var addInterlocutorAsMateCallFlag: Boolean = false,
    var deleteChatCallFlag: Boolean = false
) : ViewModelMockContext<MateChatUiState>(uiState, uiOperationFlow, retrieveErrorResult) {
    override fun reset() {
        super.reset()

        uiState = MateChatUiState()

        setChatContextCallFlag = false
        getNextMessageChunkCallFlag = false
        getInterlocutorProfileCallFlag = false
        addInterlocutorAsMateCallFlag = false
        deleteChatCallFlag = false
    }
}