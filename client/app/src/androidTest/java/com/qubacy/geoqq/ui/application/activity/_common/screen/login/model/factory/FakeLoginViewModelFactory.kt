package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory.FakeStatefulViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.TestLoginUiState
import org.mockito.Mockito

class FakeLoginViewModelFactory(

) : FakeStatefulViewModelFactory<LoginUiState>() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = super.create(modelClass) as LoginViewModel
        val uiState = TestLoginUiState()

        Mockito.`when`(viewModel.uiState).thenReturn(uiState)
        Mockito.`when`(viewModel.isLoginValid(Mockito.anyString())).thenCallRealMethod()
        Mockito.`when`(viewModel.isPasswordValid(Mockito.anyString())).thenCallRealMethod()
        Mockito.`when`(viewModel.setLoginMode(AnyMockUtil.anyObject())).thenAnswer {
            uiState.setLoginModeCallFlag = true

            Unit
        }
        Mockito.`when`(viewModel.signIn()).thenAnswer {
            uiState.signInWithTokenCallFlag = true

            Unit
        }
        Mockito.`when`(viewModel.signIn(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            uiState.signInWithLoginDataCallFlag = true

            Unit
        }
        Mockito.`when`(viewModel.signUp(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            uiState.signUpCallFlag = true

            Unit
        }

        return viewModel as T
    }
}