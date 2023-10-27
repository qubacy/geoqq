package com.qubacy.geoqq.ui.screen.geochat.auth.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.common.error.local.LocalError
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.auth.operation.AuthorizeOperation
import com.qubacy.geoqq.data.common.auth.state.AuthState
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class AuthFragmentTest {
    class AuthUiStateTestData(
        private val mModel: AuthViewModel,
        private val mAuthStateFlow: MutableStateFlow<AuthState>
    ) {
        fun setAuthorized() {
            val operations = listOf(
                AuthorizeOperation()
            )

            val authorizedState = AuthState(true, operations)

            runBlocking {
                mAuthStateFlow.emit(authorizedState)
            }
        }

        fun showError(error: LocalError) {
            val operations = listOf(
                HandleErrorOperation(error)
            )

            val authorizedState = AuthState(false, operations)

            runBlocking {
                mAuthStateFlow.emit(authorizedState)
            }
        }
    }
}