package com.qubacy.geoqq.ui.screen.geochat.signup.model

import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.ui.common.fragment.model.BaseUiState

class SignUpUiState(
    val isSignedUp: Boolean = false,
    error: Error? = null
) : BaseUiState(error) {

}