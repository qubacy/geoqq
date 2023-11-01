package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenExistenceResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
import com.qubacy.geoqq.domain.geochat.signin.operation.ApproveSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.operation.DeclineAutomaticSignInOperation
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.doReturn

@RunWith(JUnit4::class)
class SignInUseCaseTest {
    private lateinit var mSignInUseCase: SignInUseCase

    private fun initSignInUseCase(
        checkRefreshTokenExistenceResult: CheckRefreshTokenExistenceResult = CheckRefreshTokenExistenceResult(true),
        checkRefreshTokenValidityResult: CheckRefreshTokenValidityResult = CheckRefreshTokenValidityResult(
            true
        ),
        signInWithLoginPasswordResult: SignInWithLoginPasswordResult = SignInWithLoginPasswordResult(
            String(), String()
        )
    ) {
        runBlocking {
            val tokenDataRepositoryMock = Mockito.mock(TokenDataRepository::class.java)

            doReturn(checkRefreshTokenExistenceResult)
                .`when`(tokenDataRepositoryMock).checkLocalRefreshTokenExistence()
            doReturn(checkRefreshTokenValidityResult)
                .`when`(tokenDataRepositoryMock).checkRefreshTokenValidity()

            val signInDataRepositoryMock = Mockito.mock(SignInDataRepository::class.java)

            doReturn(signInWithLoginPasswordResult)
                .`when`(signInDataRepositoryMock).signInWithLoginPassword(String(), String())

            mSignInUseCase = SignInUseCase(tokenDataRepositoryMock, signInDataRepositoryMock)
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

            val approveSignInOperation = state!!.newOperations.find {
                it::class == ApproveSignInOperation::class
            }

            Assert.assertNotNull(approveSignInOperation)
        }
    }

    @Test
    fun signInWithRefreshTokenTest() {
        val refreshToken = "refreshToken"

        initSignInUseCase(
            checkRefreshTokenExistenceResult = CheckRefreshTokenExistenceResult(true),
            checkRefreshTokenValidityResult = CheckRefreshTokenValidityResult(true)
        )

        runBlocking {
            mSignInUseCase.signInWithLocalToken()

            val state = mSignInUseCase.stateFlow.value

            Assert.assertNotNull(state)

            val approveSignInOperation = state!!.newOperations.find {
                it::class == ApproveSignInOperation::class
            }

            Assert.assertNotNull(approveSignInOperation)
        }
    }

    @Test
    fun signInWithRefreshTokenWithoutRefreshTokenTest() {
        initSignInUseCase(
            checkRefreshTokenExistenceResult = CheckRefreshTokenExistenceResult(false)
        )

        runBlocking {
            mSignInUseCase.signInWithLocalToken()

            val state = mSignInUseCase.stateFlow.value

            Assert.assertNotNull(state)

            val declineAutomaticSignInOperation = state!!.newOperations.find {
                it::class == DeclineAutomaticSignInOperation::class
            }

            Assert.assertNotNull(declineAutomaticSignInOperation)
        }
    }

    @Test
    fun signInWithRefreshTokenWithInvalidRefreshTokenTest() {
        initSignInUseCase(
            checkRefreshTokenExistenceResult = CheckRefreshTokenExistenceResult(true),
            checkRefreshTokenValidityResult = CheckRefreshTokenValidityResult(false)
        )

        runBlocking {
            mSignInUseCase.signInWithLocalToken()

            val state = mSignInUseCase.stateFlow.value

            Assert.assertNotNull(state)

            val handleErrorOperation = state!!.newOperations.find {
                it::class == HandleErrorOperation::class
            }

            Assert.assertNotNull(handleErrorOperation)
        }
    }
}