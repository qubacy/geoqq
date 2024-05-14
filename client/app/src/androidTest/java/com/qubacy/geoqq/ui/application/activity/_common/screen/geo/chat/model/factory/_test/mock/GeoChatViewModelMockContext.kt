package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory._test.mock

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.factory._test.mock.BusinessViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class GeoChatViewModelMockContext(
    uiState: GeoChatUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var isLocationContextSet: Boolean? = null,
    var getLocalUserId: Long? = null,
    var areMessagesLoaded: Boolean? = null,
    var isMessageTextValid: Boolean? = null,
    var setLocationContextCallFlag: Boolean = false,
    var isLocationContextSetCallFlag: Boolean = false,
    var getLocalUserIdCallFlag: Boolean = false,
    var getMessagesCallFlag: Boolean = false,
    var areMessagesLoadedCallFlag: Boolean = false,
    var isMessageTextValidCallFlag: Boolean = false,
    var getUserProfileByMessagePositionCallFlag: Boolean = false,
    var addInterlocutorAsMateCallFlag: Boolean = false,
    var sendMessageCallFlag: Boolean = false,
    var resetMessagesCallFlag: Boolean = false,
    var changeLastLocationCallFlag: Boolean = false
) : BusinessViewModelMockContext<GeoChatUiState>(
    uiState, uiOperationFlow, retrieveErrorResult
) {
    override fun reset() {
        super.reset()

        uiState = GeoChatUiState()

        isLocationContextSet = null
        getLocalUserId = null
        areMessagesLoaded = null
        isMessageTextValid = null

        setLocationContextCallFlag = false
        isLocationContextSetCallFlag = false
        getLocalUserIdCallFlag = false
        getMessagesCallFlag = false
        areMessagesLoadedCallFlag = false
        isMessageTextValidCallFlag = false
        getUserProfileByMessagePositionCallFlag = false
        addInterlocutorAsMateCallFlag = false
        sendMessageCallFlag = false
        resetMessagesCallFlag = false
        changeLastLocationCallFlag = false
    }
}