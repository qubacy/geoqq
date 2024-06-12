package com.qubacy.geoqq.domain.login.usecase.impl

import app.cash.turbine.test
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common._test.mock.AuthDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.base.UseCaseTest
import com.qubacy.geoqq.domain.login.usecase._common.result.SignedInDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LoginUseCaseImplTest : UseCaseTest<LoginUseCaseImpl>() {
    private lateinit var mAuthDataRepositoryMockContainer: AuthDataRepositoryMockContainer

    override fun clear() {
        super.clear()

        mAuthDataRepositoryMockContainer.clear()
    }

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mAuthDataRepositoryMockContainer = AuthDataRepositoryMockContainer()

        return superDependencies.plus(mAuthDataRepositoryMockContainer.authDataRepositoryMock)
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = LoginUseCaseImpl(
            dependencies[0] as LocalErrorDatabaseDataSource,
            dependencies[1] as AuthDataRepository
        )
    }

    @Test
    fun signInWithTokenSucceededTest() = runTest {
        mUseCase.resultFlow.test {
            mUseCase.signIn()

            val result = awaitItem()

            Assert.assertEquals(SignedInDomainResult::class, result::class)
            Assert.assertTrue(mAuthDataRepositoryMockContainer.signInWithTokenCallFlag)
            Assert.assertTrue(result.isSuccessful())
        }
    }

    @Test
    fun signInWithTokenFailedTest() = runTest {
        val expectedError = TestError.normal

        mAuthDataRepositoryMockContainer.error = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signIn()

            val result = awaitItem()

            Assert.assertTrue(mAuthDataRepositoryMockContainer.signInWithTokenCallFlag)
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

            Assert.assertEquals(SignedInDomainResult::class, result::class)
            Assert.assertTrue(mAuthDataRepositoryMockContainer.signInWithLoginDataCallFlag)
            Assert.assertTrue(result.isSuccessful())
        }
    }

    @Test
    fun signInWithLoginDataFailedTest() = runTest {
        val login = "login"
        val password = "password"

        val expectedError = TestError.normal

        mAuthDataRepositoryMockContainer.error = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signIn(login, password)

            val result = awaitItem()

            Assert.assertEquals(SignedInDomainResult::class, result::class)
            Assert.assertTrue(mAuthDataRepositoryMockContainer.signInWithLoginDataCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(expectedError, result.error)
        }
    }

    @Test
    fun signUpTestSucceeded() = runTest {
        val login = "login"
        val password = "password"

        mUseCase.resultFlow.test {
            mUseCase.signUp(login, password)

            val result = awaitItem()

            Assert.assertEquals(SignedInDomainResult::class, result::class)
            Assert.assertTrue(mAuthDataRepositoryMockContainer.signUpCallFlag)
            Assert.assertTrue(result.isSuccessful())
        }
    }

    @Test
    fun signUpTestFailed() = runTest {
        val login = "login"
        val password = "password"

        val expectedError = TestError.normal

        mAuthDataRepositoryMockContainer.error = expectedError

        mUseCase.resultFlow.test {
            mUseCase.signUp(login, password)

            val result = awaitItem()

            Assert.assertEquals(SignedInDomainResult::class, result::class)
            Assert.assertTrue(mAuthDataRepositoryMockContainer.signUpCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(expectedError, result.error)
        }
    }
}