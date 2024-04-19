package com.qubacy.geoqq.domain.login.usecase

import app.cash.turbine.test
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.data.token.repository.result.GetTokensDataResult
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.login.usecase.result.SignedInDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LoginUseCaseTest : UseCaseTest<LoginUseCase>() {
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer

    override fun clear() {
        super.clear()

        mTokenDataRepositoryMockContainer.clear()
    }

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()

        return superDependencies.plus(mTokenDataRepositoryMockContainer.tokenDataRepositoryMock)
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = LoginUseCase(
            dependencies[0] as ErrorDataRepository,
            dependencies[1] as TokenDataRepository
        )
    }

    @Test
    fun signInWithTokenSucceededTest() = runTest {
        val expectedGetTokensDataResult =
            GetTokensDataResult("accessToken", "refreshToken")

        mTokenDataRepositoryMockContainer.getTokensDataResult = expectedGetTokensDataResult

        mUseCase.resultFlow.test {
            mUseCase.signIn()

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositoryMockContainer.signInWithTokenCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signInWithTokenFailedTest() = runTest {
        val expectedError = TestError.normal

        mTokenDataRepositoryMockContainer.error = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signIn()

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositoryMockContainer.signInWithTokenCallFlag)
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

            Assert.assertTrue(mTokenDataRepositoryMockContainer.signInWithLoginDataCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signInWithLoginDataFailedTest() = runTest {
        val login = "login"
        val password = "password"

        val expectedError = TestError.normal

        mTokenDataRepositoryMockContainer.error = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signIn(login, password)

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositoryMockContainer.signInWithLoginDataCallFlag)
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

            Assert.assertTrue(mTokenDataRepositoryMockContainer.signUpCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }

    @Test
    fun signUpTestFailed() = runTest {
        val login = "login"
        val password = "password"

        val expectedError = TestError.normal

        mTokenDataRepositoryMockContainer.error = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signUp(login, password)

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositoryMockContainer.signUpCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(expectedError, result.error)
            Assert.assertEquals(SignedInDomainResult::class, result::class)
        }
    }
}