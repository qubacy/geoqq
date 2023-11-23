package com.qubacy.geoqq.ui.screen.geochat.auth.signup.model

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
import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase
import com.qubacy.geoqq.domain.geochat.signup.operation.ApproveSignUpOperation
import com.qubacy.geoqq.domain.geochat.signup.state.SignUpState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.operation.PassSignUpUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.state.SignUpUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class SignUpViewModel(
    private val mSignUpUseCase: SignUpUseCase
) : WaitingViewModel() {
    private var mSignUpStateFlow = mSignUpUseCase.stateFlow

    private var mSignUpUiStateFlow = mSignUpStateFlow.map { stateToUiState(it) }
    val signUpUiStateFlow: LiveData<SignUpUiState?> = mSignUpUiStateFlow.asLiveData()

    init {
        mSignUpUseCase.setCoroutineScope(viewModelScope)
    }

    private fun stateToUiState(signUpState: SignUpState?): SignUpUiState? {
        if (mIsWaiting.value == true) mIsWaiting.value = false
        if (signUpState == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in signUpState.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return SignUpUiState(uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            ApproveSignUpOperation::class -> {
                val approveSignUpOperation = operation as ApproveSignUpOperation

                PassSignUpUiOperation()
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

    fun isSignUpDataCorrect(
        username: String,
        password: String,
        confirmationPassword: String
    ): Boolean {
        if (!isSignUpDataFull(username, password, confirmationPassword)
         || (password != confirmationPassword))
        {
            return false
        }

        return UsernameValidator().check(username)
            && StandardPasswordValidator().check(password)
            && StandardPasswordValidator().check(confirmationPassword)
    }

    private fun isSignUpDataFull(
        username: String,
        password: String,
        confirmationPassword: String
    ): Boolean {
        return (username.isNotEmpty() && password.isNotEmpty() && confirmationPassword.isNotEmpty())
    }

    fun signUp(
        login: String,
        password: String,
        confirmationPassword: String
    ) {
        mIsWaiting.value = true

        mSignUpUseCase.signUp(login, password)
    }

    fun interruptSignUp() {
        mIsWaiting.value = false

        mSignUpUseCase.interruptOperation()
    }

    override fun retrieveError(errorId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mSignUpUseCase.getError(errorId)
        }
    }
}

class SignUpViewModelFactory(
    private val mSignUpUseCase: SignUpUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignUpViewModel::class.java))
            throw IllegalArgumentException()

        return SignUpViewModel(mSignUpUseCase) as T
    }
}