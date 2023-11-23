package com.qubacy.geoqq.ui.screen.geochat.auth.signin.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.domain.common.model.validator.password.standard.StandardPasswordValidator
import com.qubacy.geoqq.data.common.entity.person.common.validator.username.UsernameValidator
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.operation.interrupt.InterruptOperation
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signin.operation.ProcessSignInResultOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.operation.PassSignInUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.state.SignInUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class SignInViewModel(
    private val mSignInUseCase: SignInUseCase
) : WaitingViewModel() {
    private var mSignInStateFlow = mSignInUseCase.stateFlow

    private var mSignInUiStateFlow = mSignInStateFlow.map { stateToUiState(it) }
    val signInUiStateFlow: LiveData<SignInUiState?> = mSignInUiStateFlow.asLiveData()

    init {
        mSignInUseCase.setCoroutineScope(viewModelScope)
    }

    fun isSignInDataCorrect(
        login: String,
        password: String
    ): Boolean {
        if (!isSignInDataFull(login, password))
            return false

        return UsernameValidator().check(login)
            && StandardPasswordValidator().check(password)
    }

    private fun isSignInDataFull(
        login: String,
        password: String
    ): Boolean {
        return (login.isNotEmpty() && password.isNotEmpty())
    }

    fun signIn(
        login: String,
        password: String
    ) {
        mIsWaiting.value = true

        mSignInUseCase.signInWithLoginPassword(login, password)
    }

    fun signIn() {
        mIsWaiting.value = true

        mSignInUseCase.signInWithLocalToken()
    }

    fun interruptSignIn() {
        mIsWaiting.value = false

        mSignInUseCase.interruptOperation()
    }

    private fun stateToUiState(state: SignInState?): SignInUiState? {
        if (isWaiting.value == true) mIsWaiting.value = false
        if (state == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in state.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return SignInUiState(uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            ProcessSignInResultOperation::class -> {
                val processSignInResultOperation = operation as ProcessSignInResultOperation

                if (processSignInResultOperation.isSignedIn) PassSignInUiOperation()
                else null
            }
            InterruptOperation::class -> {
                val interruptOperation = operation as InterruptOperation

                null
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    override fun retrieveError(errorId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mSignInUseCase.getError(errorId)
        }
    }
}

class SignInViewModelFactory(
    private val mSignInUseCase: SignInUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignInViewModel::class.java))
            throw IllegalArgumentException()

        return SignInViewModel(mSignInUseCase) as T
    }
}