package com.qubacy.geoqq.domain.login.usecase

import app.cash.turbine.test
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensDataResult
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.login.usecase.result.SignedInDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class LoginUseCaseTest : UseCaseTest<LoginUseCase>() {
    private var mGetTokensDataResult: GetTokensDataResult? = null

    private var mTokenDataRepositoryGetTokensCallFlag = false
    private var mTokenDataRepositorySignInCallFlag = false
    private var mTokenDataRepositorySignUpCallFlag = false

    override fun clear() {
        super.clear()

        mGetTokensDataResult = null

        mTokenDataRepositoryGetTokensCallFlag = false
        mTokenDataRepositorySignInCallFlag = false
        mTokenDataRepositorySignUpCallFlag = false
    }

    override fun initRepositories(): List<DataRepository> {
        val superRepositories = super.initRepositories()
        val tokenDataRepositoryMock = Mockito.mock(TokenDataRepository::class.java)

        runTest {
            Mockito.`when`(tokenDataRepositoryMock.getTokens()).thenAnswer {
                mTokenDataRepositoryGetTokensCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)

                mGetTokensDataResult
            }
            Mockito.`when`(tokenDataRepositoryMock.signIn(
                Mockito.anyString(), Mockito.anyString()
            )).thenAnswer {
                mTokenDataRepositorySignInCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)
            }
            Mockito.`when`(tokenDataRepositoryMock.signUp(
                Mockito.anyString(), Mockito.anyString()
            )).thenAnswer {
                mTokenDataRepositorySignUpCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)
            }
        }

        return superRepositories.plus(tokenDataRepositoryMock)
    }

    override fun initUseCase(repositories: List<DataRepository>) {
        mUseCase = LoginUseCase(
            repositories[0] as ErrorDataRepository,
            repositories[1] as TokenDataRepository
        )
    }

    @Test
    fun signInWithTokenSucceededTest() = runTest {
        val expectedGetTokensDataResult = GetTokensDataResult(
            "accessToken",
            "refreshToken"
        )

        mGetTokensDataResult = expectedGetTokensDataResult

        mUseCase.resultFlow.test {
            mUseCase.signIn()

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositoryGetTokensCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signInWithTokenFailedTest() = runTest {
        val expectedError = TestError.normal

        mErrorDataRepositoryMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signIn()

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositoryGetTokensCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(expectedError, result.error)
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signInWithLoginDataSucceededTest() = runTest {
        val login = "login"
        val password = "password"

        mUseCase.resultFlow.test {
            mUseCase.signIn(login, password)

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositorySignInCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signInWithLoginDataFailedTest() = runTest {
        val expectedError = TestError.normal
        val login = "login"
        val password = "password"

        mErrorDataRepositoryMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signIn(login, password)

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositorySignInCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(expectedError, result.error)
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signUpTestSucceeded() = runTest {
        val login = "login"
        val password = "password"

        mUseCase.resultFlow.test {
            mUseCase.signUp(login, password)

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositorySignUpCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signUpTestFailed() = runTest {
        val expectedError = TestError.normal
        val login = "login"
        val password = "password"

        mErrorDataRepositoryMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signUp(login, password)

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositorySignUpCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(expectedError, result.error)
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }
}