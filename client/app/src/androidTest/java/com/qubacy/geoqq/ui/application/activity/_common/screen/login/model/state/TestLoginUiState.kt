package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.TestUiState

class TestLoginUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    loginMode: LoginMode = LoginMode.SIGN_IN,
    autoSignInAllowed: Boolean = true,
    var setLoginModeCallFlag: Boolean = false,
    var signInWithTokenCallFlag: Boolean = false,
    var signInWithLoginDataCallFlag: Boolean = false,
    var signUpCallFlag: Boolean = false
) : LoginUiState(isLoading, error, loginMode, autoSignInAllowed), TestUiState {

}