package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.impl

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.login.usecase._common.LoginUseCase
import com.qubacy.geoqq.domain.login.usecase._common.result.SignedInDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.LoginViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.operation.SignInUiOperation
import javax.inject.Inject
import javax.inject.Qualifier

open class LoginViewModelImpl @Inject constructor(
    mSavedInstanceState: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mUseCase: LoginUseCase
) : LoginViewModel(mSavedInstanceState, mErrorSource, mUseCase) {
    companion object {
        const val TAG = "LoginViewModel"
    }

    override fun onLoginSignedIn(
        signedInResult: SignedInDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)
        changeAutoSignInAllowedState(false)

        if (!signedInResult.isSuccessful())
            return onError(signedInResult.error!!)

        return listOf(SignInUiOperation())
    }

    override fun isSignInDataValid(login: String, password: String): Boolean {
        return true
    }

    override fun isSignUpDataValid(login: String, password: String, passwordAgain: String): Boolean {
        return (password == passwordAgain)
    }

    override fun signIn() {
        changeLoadingState(true)

        mUseCase.signIn()
    }

    override fun signIn(login: String, password: String) {
        changeLoadingState(true)

        mUseCase.signIn(login, password)
    }

    override fun signUp(login: String, password: String) {
        changeLoadingState(true)

        mUseCase.signUp(login, password)
    }

    private fun changeAutoSignInAllowedState(isAllowed: Boolean) {
        mUiState.autoSignInAllowed = isAllowed
    }
}

@Qualifier
annotation class LoginViewModelFactoryQualifier

class LoginViewModelImplFactory(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mLoginUseCase: LoginUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(LoginViewModelImpl::class.java))
            throw IllegalArgumentException()

        return LoginViewModelImpl(handle, mErrorSource, mLoginUseCase) as T
    }
}