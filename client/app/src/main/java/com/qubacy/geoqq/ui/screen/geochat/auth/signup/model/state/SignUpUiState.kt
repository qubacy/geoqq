package com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.state

import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.ui.common.fragment.common.model.state.BaseUiState

class SignUpUiState(
    val isSignedUp: Boolean = false,
    error: Error? = null
) : BaseUiState(error) {

}