package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory._test.mock

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.factory._test.mock.BusinessViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class MateChatViewModelMockContext(
    uiState: MateChatUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var isInterlocutorMate: Boolean = false,
    var isInterlocutorChatable: Boolean = false,
    var isInterlocutorMateable: Boolean = false,
    var isInterlocutorMateableOrDeletable: Boolean = false,
    var isChatDeletable: Boolean = false,
    var getInterlocutorProfile: UserPresentation? = null,
    var setChatContextCallFlag: Boolean = false,
    var getNextMessageChunkCallFlag: Boolean = false,
    var getInterlocutorProfileCallFlag: Boolean = false,
    var addInterlocutorAsMateCallFlag: Boolean = false,
    var deleteChatCallFlag: Boolean = false
) : BusinessViewModelMockContext<MateChatUiState>(
    uiState, uiOperationFlow, retrieveErrorResult
) {
    override fun reset() {
        super.reset()

        uiState = MateChatUiState()

        isInterlocutorChatable = false
        isInterlocutorMateable = false
        isInterlocutorMateableOrDeletable = false
        isChatDeletable = false

        setChatContextCallFlag = false
        getNextMessageChunkCallFlag = false
        getInterlocutorProfileCallFlag = false
        addInterlocutorAsMateCallFlag = false
        deleteChatCallFlag = false
    }
}