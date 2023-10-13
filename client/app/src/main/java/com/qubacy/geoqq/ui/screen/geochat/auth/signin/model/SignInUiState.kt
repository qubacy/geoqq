package com.qubacy.geoqq.ui.screen.geochat.auth.signin.model

import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.ui.common.fragment.common.model.BaseUiState

class SignInUiState(
    val isSignedIn: Boolean = false,
    error: Error? = null
) : BaseUiState(error) {

}