package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory._test.mock

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class LoginViewModelMockContext(
    uiState: LoginUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var setLoginModeCallFlag: Boolean = false,
    var signInWithTokenCallFlag: Boolean = false,
    var signInWithLoginDataCallFlag: Boolean = false,
    var signUpCallFlag: Boolean = false
) : ViewModelMockContext<LoginUiState>(uiState, uiOperationFlow, retrieveErrorResult) {
    override fun reset() {
        super.reset()

        uiState = LoginUiState()

        setLoginModeCallFlag = false
        signInWithTokenCallFlag = false
        signInWithLoginDataCallFlag = false
        signUpCallFlag = false
    }
}