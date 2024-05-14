package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.factory.FakeBusinessViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.LoginViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory._test.mock.LoginViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.state.LoginUiState
import org.mockito.Mockito

class FakeLoginViewModelFactory(
    mockContext: LoginViewModelMockContext
) : FakeBusinessViewModelFactory<
    LoginUiState, LoginViewModel, LoginViewModelMockContext
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as LoginViewModel

        Mockito.`when`(viewModelMock.isSignInDataValid(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer { true }
        Mockito.`when`(viewModelMock.isSignUpDataValid(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString()
        )).thenAnswer { true }
        Mockito.`when`(viewModelMock.setLoginMode(AnyMockUtil.anyObject())).thenAnswer {
            val loginMode = it.arguments[0] as LoginUiState.LoginMode

            mockContext.uiState.loginMode = loginMode
            mockContext.setLoginModeCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.signIn()).thenAnswer {
            mockContext.signInWithTokenCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.signIn(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mockContext.signInWithLoginDataCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.signUp(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mockContext.signUpCallFlag = true

            Unit
        }

        return viewModelMock as T
    }
}