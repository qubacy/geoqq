package com.qubacy.geoqq.ui.application.activity._common.screen.login.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.login.usecase.LoginUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class LoginViewModel @Inject constructor(
    mSavedInstanceState: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    private val mLoginUseCase: LoginUseCase
) : BusinessViewModel<LoginUiState>(
    mSavedInstanceState, mErrorDataRepository, mLoginUseCase
) {
    override fun generateDefaultUiState(): LoginUiState {
        return LoginUiState()
    }

    open fun setLoginMode(loginMode: LoginUiState.LoginMode) {
        mUiState.loginMode = loginMode
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