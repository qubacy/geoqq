package com.qubacy.geoqq.ui.application.activity._common.screen.login.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.login.usecase.LoginUseCase
import com.qubacy.geoqq.domain.login.usecase.result.SignedInDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.operation.SignInUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.result.handler.LoginDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class LoginViewModel @Inject constructor(
    mSavedInstanceState: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mUseCase: LoginUseCase
) : BusinessViewModel<LoginUiState, LoginUseCase>(
    mSavedInstanceState, mErrorDataRepository, mUseCase
) {
    companion object {
        const val TAG = "LoginViewModel"
    }

    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(LoginDomainResultHandler(this))
    }

    override fun generateDefaultUiState(): LoginUiState {
        return LoginUiState()
    }

    fun onLoginSignedIn(
        signedInResult: SignedInDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)
        changeAutoSignInAllowedState(false)

        if (!signedInResult.isSuccessful())
            return onError(signedInResult.error!!)

        return listOf(SignInUiOperation())
    }

    private fun changeAutoSignInAllowedState(isAllowed: Boolean) {
        mUiState.autoSignInAllowed = isAllowed
    }

    open fun setLoginMode(loginMode: LoginUiState.LoginMode) {
        mUiState.loginMode = loginMode
    }

    open fun isSignInDataValid(login: String, password: String): Boolean {
        return true
    }

    open fun isSignUpDataValid(login: String, password: String, passwordAgain: String): Boolean {
        return (password == passwordAgain)
    }

    open fun signIn() {
        changeLoadingState(true)

        mUseCase.signIn()
    }

    open fun signIn(login: String, password: String) {
        changeLoadingState(true)

        mUseCase.signIn(login, password)
    }

    open fun signUp(login: String, password: String) {
        changeLoadingState(true)

        mUseCase.signUp(login, password)
    }
}

@Qualifier
annotation class LoginViewModelFactoryQualifier

class LoginViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mLoginUseCase: LoginUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(LoginViewModel::class.java))
            throw IllegalArgumentException()

        return LoginViewModel(handle, mErrorDataRepository, mLoginUseCase) as T
    }
}