package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory._test.mock

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.factory._test.mock.BusinessViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.state.LoginUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class LoginViewModelMockContext(
    uiState: LoginUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var setLoginModeCallFlag: Boolean = false,
    var signInWithTokenCallFlag: Boolean = false,
    var signInWithLoginDataCallFlag: Boolean = false,
    var signUpCallFlag: Boolean = false
) : BusinessViewModelMockContext<LoginUiState>(
    uiState, uiOperationFlow, retrieveErrorResult
) {
    override fun reset() {
        super.reset()

        uiState = LoginUiState()

        setLoginModeCallFlag = false
        signInWithTokenCallFlag = false
        signInWithLoginDataCallFlag = false
        signUpCallFlag = false
    }
}