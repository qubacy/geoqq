package com.qubacy.geoqq.domain.geochat.signup

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.signup.repository.result.SignUpResult
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
        signUpResult: Result = SignUpResult()
    ) {
        runBlocking {
            val signUpDataRepositoryMock = Mockito.mock(SignUpDataRepository::class.java)

            Mockito.doReturn(signUpResult)
                .`when`(signUpDataRepositoryMock).signUp(String(), String())

            mSignUpUseCase = SignUpUseCase(signUpDataRepositoryMock)
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