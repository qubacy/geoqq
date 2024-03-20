package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory.FakeStatefulViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory._test.mock.LoginViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import org.mockito.Mockito

class FakeLoginViewModelFactory(
    mockContext: LoginViewModelMockContext
) : FakeStatefulViewModelFactory<
    LoginUiState, LoginViewModel, LoginViewModelMockContext
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as LoginViewModel

        Mockito.`when`(viewModelMock.isLoginValid(Mockito.anyString())).thenCallRealMethod()
        Mockito.`when`(viewModelMock.isPasswordValid(Mockito.anyString())).thenCallRealMethod()
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