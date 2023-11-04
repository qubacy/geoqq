package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.geochat.signin.operation.ProcessSignInResultOperation
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doReturn

@RunWith(JUnit4::class)
class SignInUseCaseTest {
    private lateinit var mSignInUseCase: SignInUseCase

    private fun initSignInUseCase(
        getTokensResult: GetTokensResult = GetTokensResult(String(), String()),
        signInWithLoginPasswordResult: SignInWithLoginPasswordResult = SignInWithLoginPasswordResult(
            String(), String()
        )
    ) {
        runBlocking {
            val tokenDataRepositoryMock = Mockito.mock(TokenDataRepository::class.java)

            doReturn(getTokensResult).`when`(tokenDataRepositoryMock).getTokens()

            val signInDataRepositoryMock = Mockito.mock(SignInDataRepository::class.java)

            Mockito.`when`(signInDataRepositoryMock.signInWithLoginPassword(anyString(), anyString()))
                .thenReturn(signInWithLoginPasswordResult)

            val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

            mSignInUseCase = SignInUseCase(
                tokenDataRepositoryMock, signInDataRepositoryMock, errorDataRepository)
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

            val state = mSignInUseCase.stateFlow.value

            Assert.assertNotNull(state)

            val processSignInResultOperation = state!!.newOperations.find {
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

            val state = mSignInUseCase.stateFlow.value

            Assert.assertNotNull(state)

            val processSignInResultOperation = state!!.newOperations.find {
                it::class == ProcessSignInResultOperation::class
            } as ProcessSignInResultOperation

            Assert.assertNotNull(processSignInResultOperation)
            Assert.assertTrue(processSignInResultOperation.isSignedIn)
        }
    }
}