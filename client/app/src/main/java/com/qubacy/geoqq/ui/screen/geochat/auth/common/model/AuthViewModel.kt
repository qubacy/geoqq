package com.qubacy.geoqq.ui.screen.geochat.auth.common.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.geochat.auth.common.operation.AuthorizeOperation
import com.qubacy.geoqq.data.geochat.auth.common.state.AuthState
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.operation.AuthorizeUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.state.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

// todo: providing a data repository as an argument..
abstract class AuthViewModel : WaitingViewModel() {
    // todo: assign to the repository's flow:
    protected var mAuthStateFlow = MutableStateFlow<AuthState?>(null)

    protected var mAuthUiStateFlow = mAuthStateFlow.map { stateToUiState(it) }
    val authUiState: LiveData<AuthUiState?> = mAuthUiStateFlow.asLiveData()

    protected open fun stateToUiState(state: AuthState?): AuthUiState? {
        mIsWaiting.value = false

        if (state == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in state.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return AuthUiState(state.isAuthorized, state.authToken, uiOperations)
    }

    protected open fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class.java) {
            AuthorizeOperation::class.java -> {
                val authorizeOperation = operation as AuthorizeOperation

                AuthorizeUiOperation()
            }
            HandleErrorOperation::class.java -> {
                val handleErrorOperation = operation as HandleErrorOperation

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }
}