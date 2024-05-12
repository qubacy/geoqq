package com.qubacy.geoqq.domain.logout.usecase.impl

import app.cash.turbine.test
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common._test.mock.AuthDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.result.LogoutDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LogoutUseCaseImplTest() : UseCaseTest<LogoutUseCase>() {
    private lateinit var mAuthDataRepositoryMockContainer: AuthDataRepositoryMockContainer

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mAuthDataRepositoryMockContainer = AuthDataRepositoryMockContainer()

        return superDependencies
            .plus(mAuthDataRepositoryMockContainer.authDataRepositoryMock)
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = LogoutUseCaseImpl(
            dependencies[0] as LocalErrorDatabaseDataSource,
            dependencies[1] as AuthDataRepository
        )
    }

    @Test
    fun logoutTest() = runTest {
        mUseCase.resultFlow.test {
            mUseCase.logout()

            val result = awaitItem()

            Assert.assertTrue(mAuthDataRepositoryMockContainer.logoutCallFlag)
            Assert.assertEquals(LogoutDomainResult::class, result::class)
        }
    }
}