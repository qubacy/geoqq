package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class MateChatsViewModelMockContext(
    uiState: MateChatsUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var getNextChatChunkCallFlag: Boolean = false
) : ViewModelMockContext<MateChatsUiState>(uiState, uiOperationFlow, retrieveErrorResult) {
    override fun reset() {
        super.reset()

        uiState = MateChatsUiState()

        getNextChatChunkCallFlag = false
    }
}