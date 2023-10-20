package com.qubacy.geoqq.ui.screen.geochat.auth.common.model.state

import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.state.OperationUiState

class AuthUiState(
    val isAuthorized: Boolean,
    val authToken: String,
    newUiOperations: List<UiOperation>
) : OperationUiState(newUiOperations) {

}