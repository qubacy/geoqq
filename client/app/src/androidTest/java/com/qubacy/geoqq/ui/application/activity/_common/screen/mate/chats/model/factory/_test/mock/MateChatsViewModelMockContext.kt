package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.factory._test.mock.BusinessViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class MateChatsViewModelMockContext(
    uiState: MateChatsUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var prepareChatForEntering: MateChatPresentation? = null,
    var getNextChatChunkCallFlag: Boolean = false,
    var prepareChatForEnteringCallFlag: Boolean = false
) : BusinessViewModelMockContext<MateChatsUiState>(
    uiState, uiOperationFlow, retrieveErrorResult
) {
    override fun reset() {
        super.reset()

        uiState = MateChatsUiState()

        prepareChatForEntering = null

        getNextChatChunkCallFlag = false
        prepareChatForEnteringCallFlag = false
    }
}