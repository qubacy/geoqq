package com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.login.usecase._common.LoginUseCase
import com.qubacy.geoqq.domain.login.usecase._common.result.SignedInDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.result.handler.LoginDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.state.LoginUiState

abstract class LoginViewModel(
    mSavedInstanceState: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mUseCase: LoginUseCase
) : BusinessViewModel<LoginUiState, LoginUseCase>(
    mSavedInstanceState, mErrorSource, mUseCase
) {
    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(LoginDomainResultHandler(this))
    }
    override fun generateDefaultUiState(): LoginUiState {
        return LoginUiState()
    }
    abstract fun onLoginSignedIn(
        signedInResult: SignedInDomainResult
    ): List<UiOperation>
    abstract fun isSignInDataValid(login: String, password: String): Boolean
    abstract fun isSignUpDataValid(login: String, password: String, passwordAgain: String): Boolean
    abstract fun signIn()
    abstract fun signIn(login: String, password: String)
    abstract fun signUp(login: String, password: String)
    open fun setLoginMode(loginMode: LoginUiState.LoginMode) {
        mUiState.loginMode = loginMode
    }
}