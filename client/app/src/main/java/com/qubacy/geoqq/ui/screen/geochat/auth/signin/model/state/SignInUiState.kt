package com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.state

import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.ui.common.fragment.common.model.state.BaseUiState

class SignInUiState(
    val isSignedIn: Boolean = false,
    error: Error? = null
) : BaseUiState(error) {

}