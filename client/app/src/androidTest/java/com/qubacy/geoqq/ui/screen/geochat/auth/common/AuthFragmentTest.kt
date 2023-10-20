package com.qubacy.geoqq.ui.screen.geochat.auth.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.geochat.auth.common.operation.AuthorizeOperation
import com.qubacy.geoqq.data.geochat.auth.common.state.AuthState
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.AuthViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class AuthFragmentTest {
    class AuthUiStateTestData(
        private val mModel: AuthViewModel,
        private val mAuthStateFlow: MutableStateFlow<AuthState>
    ) {
        fun setAuthorized(accessToken: String = String()) {
            val operations = listOf(
                AuthorizeOperation()
            )

            val authorizedState = AuthState(true, accessToken, operations)

            runBlocking {
                mAuthStateFlow.emit(authorizedState)
            }
        }

        fun showError(error: Error) {
            val operations = listOf(
                HandleErrorOperation(error)
            )

            val authorizedState = AuthState(false, String(), operations)

            runBlocking {
                mAuthStateFlow.emit(authorizedState)
            }
        }
    }
}