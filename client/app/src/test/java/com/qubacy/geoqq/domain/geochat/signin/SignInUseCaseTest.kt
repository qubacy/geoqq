package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.geochat.signin.operation.ApproveLogoutOperation
import com.qubacy.geoqq.domain.geochat.signin.operation.ProcessSignInResultOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doReturn
import java.util.concurrent.atomic.AtomicReference

@RunWith(JUnit4::class)
class SignInUseCaseTest {
    private lateinit var mSignInUseCase: SignInUseCase

    private lateinit var mSignInStateAtomicRef: AtomicReference<SignInState?>

    private fun initSignInUseCase(
        getTokensResult: GetTokensResult = GetTokensResult(String(), String()),
        signInWithLoginPasswordResult: SignInWithLoginPasswordResult = SignInWithLoginPasswordResult(
            String(), String()
        )
    ) {
        runBlocking {
            val tokenDataRepositoryMock = Mockito.mock(TokenDataRepository::class.java)

            doReturn(getTokensResult).`when`(tokenDataRepositoryMock).getTokens()
            doReturn(Result()).`when`(tokenDataRepositoryMock).clearTokens()

            val signInDataRepositoryMock = Mockito.mock(SignInDataRepository::class.java)

            Mockito.`when`(signInDataRepositoryMock.signInWithLoginPassword(anyString(), anyString()))
                .thenReturn(signInWithLoginPasswordResult)

            val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

            mSignInUseCase = SignInUseCase(
                errorDataRepository, tokenDataRepositoryMock, signInDataRepositoryMock)

            mSignInStateAtomicRef = AtomicReference(null)

            GlobalScope.launch(Dispatchers.IO) {
                mSignInUseCase.stateFlow.collect {
                    if (it == null) return@collect

                    mSignInStateAtomicRef.set(it)
                }
            }
        }
    }

    @Before
    fun setup() {
        initSignInUseCase()
    }

    @Test
    fun signInWithLoginPasswordTest() {
        val login = "testtest"
        val password = "password"

        runBlocking {
            mSignInUseCase.signInWithLoginPassword(login, password)

            while (mSignInStateAtomicRef.get() == null) { }

            val curSignInState = mSignInStateAtomicRef.get()

            val processSignInResultOperation = curSignInState!!.newOperations.find {
                it::class == ProcessSignInResultOperation::class
            } as ProcessSignInResultOperation

            Assert.assertNotNull(processSignInResultOperation)
            Assert.assertTrue(processSignInResultOperation.isSignedIn)
        }
    }

    @Test
    fun signInWithLocalTokenTest() {
        initSignInUseCase(
            getTokensResult = GetTokensResult("token", "token")
        )

        runBlocking {
            mSignInUseCase.signInWithLocalToken()

            while (mSignInStateAtomicRef.get() == null) { }

            val curSignInState = mSignInStateAtomicRef.get()

            val processSignInResultOperation = curSignInState!!.newOperations.find {
                it::class == ProcessSignInResultOperation::class
            } as ProcessSignInResultOperation

            Assert.assertNotNull(processSignInResultOperation)
            Assert.assertTrue(processSignInResultOperation.isSignedIn)
        }
    }

    @Test
    fun logoutTest() {
        initSignInUseCase()

        runBlocking {
            mSignInUseCase.logout()

            while (mSignInStateAtomicRef.get() == null) { }

            val curSignInState = mSignInStateAtomicRef.get()

            val approveLogoutOperation = curSignInState!!.newOperations.find {
                it::class == ApproveLogoutOperation::class
            } as ApproveLogoutOperation

            Assert.assertNotNull(approveLogoutOperation)
        }
    }
}