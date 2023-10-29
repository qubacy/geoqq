package com.qubacy.geoqq.ui.screen.geochat.auth.signin.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.myprofile.entity.myprofile.validator.password.LoginPasswordValidator
import com.qubacy.geoqq.data.common.entity.person.common.validator.username.UsernameValidator
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signin.operation.ApproveSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.operation.DeclineAutomaticSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.operation.PassSignInUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.state.SignInUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SignInViewModel(
    private val mSignInUseCase: SignInUseCase
) : WaitingViewModel() {
    private var mSignInStateFlow = mSignInUseCase.signInStateFlow

    private var mSignInUiStateFlow = mSignInStateFlow.map { stateToUiState(it) }
    val signInUiStateFlow: LiveData<SignInUiState?> = mSignInUiStateFlow.asLiveData()

    fun isSignInDataCorrect(
        login: String,
        password: String
    ): Boolean {
        if (!isSignInDataFull(login, password))
            return false

        return UsernameValidator().check(login)
            && LoginPasswordValidator().check(password)
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
        viewModelScope.launch(Dispatchers.IO) {
            mSignInUseCase.signInWithUsernamePassword(login, password)
        }

        mIsWaiting.value = true
    }

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            mSignInUseCase.signInWithLocalToken()
        }

        mIsWaiting.value = true
    }

    fun interruptSignIn() {
        // todo: handling Sign In interruption..

        mIsWaiting.value = false
    }

    private fun stateToUiState(state: SignInState?): SignInUiState? {
        mIsWaiting.value = false

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
        return when (operation::class.java) {
            ApproveSignInOperation::class.java -> {
                val approveSignInOperation = operation as ApproveSignInOperation

                PassSignInUiOperation()
            }
            DeclineAutomaticSignInOperation::class.java -> {
                val declineAutomaticSignInOperation = operation as DeclineAutomaticSignInOperation

                mIsWaiting.value = false

                null
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

class SignInViewModelFactory(
    private val mSignInUseCase: SignInUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignInViewModel::class.java))
            throw IllegalArgumentException()

        return SignInViewModel(mSignInUseCase) as T
    }
}