package com.qubacy.geoqq.domain.geochat.signup

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.signup.repository.result.SignUpResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.domain.geochat.signup.operation.ApproveSignUpOperation
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class SignUpUseCaseTest {
    private lateinit var mSignUpUseCase: SignUpUseCase

    private fun initSignUpUseCase(
        tokenSavingResult: Result = Result(),
        signUpResult: Result = SignUpResult(String(), String())
    ) {
        runBlocking {
            val tokenDataRepositoryMock = Mockito.mock(TokenDataRepository::class.java)

            Mockito.doReturn(tokenSavingResult).`when`(tokenDataRepositoryMock)
                .saveTokens(Mockito.anyString(), Mockito.anyString())

            val signUpDataRepositoryMock = Mockito.mock(SignUpDataRepository::class.java)

            Mockito.doReturn(signUpResult)
                .`when`(signUpDataRepositoryMock).signUp(Mockito.anyString(), Mockito.anyString())

            val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

            mSignUpUseCase = SignUpUseCase(
                tokenDataRepositoryMock, signUpDataRepositoryMock, errorDataRepository)
        }
    }

    @Before
    fun setup() {
        initSignUpUseCase()
    }

    @Test
    fun signUpTest() {
        val login = "testtest"
        val password = "password"

        runBlocking {
            mSignUpUseCase.signUp(login, password)

            val state = mSignUpUseCase.stateFlow.value

            Assert.assertNotNull(state)

            val approveSignUpOperation = state!!.newOperations.find {
                it::class == ApproveSignUpOperation::class
            }

            Assert.assertNotNull(approveSignUpOperation)
        }
    }
}