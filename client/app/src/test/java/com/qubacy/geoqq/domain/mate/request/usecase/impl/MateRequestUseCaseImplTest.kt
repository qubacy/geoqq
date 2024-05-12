package com.qubacy.geoqq.domain.mate.request.usecase.impl

import app.cash.turbine.test
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._common._test.mock.MateRequestDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.interlocutor.usecase._common._test.context.InterlocutorUseCaseTestContext
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._common._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MateRequestUseCaseImplTest : UseCaseTest<MateRequestUseCaseImpl>() {
    companion object {
        val DEFAULT_USER = InterlocutorUseCaseTestContext.DEFAULT_USER
    }

    private lateinit var mMateRequestDataRepositoryMockContainer: MateRequestDataRepositoryMockContainer
    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer

    override fun clear() {
        super.clear()

        mMateRequestDataRepositoryMockContainer.clear()
        mLogoutUseCaseMockContainer.clear()
    }

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mMateRequestDataRepositoryMockContainer = MateRequestDataRepositoryMockContainer()
        mLogoutUseCaseMockContainer = LogoutUseCaseMockContainer()

        return superDependencies
            .plus(mLogoutUseCaseMockContainer.logoutUseCaseMock)
            .plus(mMateRequestDataRepositoryMockContainer.mateRequestDataRepositoryMock)
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MateRequestUseCaseImpl(
            dependencies[0] as LocalErrorDatabaseDataSource,
            dependencies[1] as LogoutUseCase,
            dependencies[2] as MateRequestDataRepository
        )
    }

    @Test
    fun sendMateRequestTest() = runTest {
        val interlocutor = DEFAULT_USER

        mUseCase.resultFlow.test {
            mUseCase.sendMateRequest(interlocutor.id)

            val result = awaitItem()

            Assert.assertTrue(mMateRequestDataRepositoryMockContainer.createMateRequestCallFlag)
            Assert.assertEquals(SendMateRequestDomainResult::class, result::class)
            Assert.assertTrue(result.isSuccessful())
        }
    }

    @Test
    fun answerMateRequestTest() = runTest {
        val requestId = 0L
        val isAccepted = false

        mUseCase.resultFlow.test {
            mUseCase.answerMateRequest(requestId, isAccepted)

            val result = awaitItem()

            Assert.assertTrue(mMateRequestDataRepositoryMockContainer.answerMateRequestCallFlag)
            Assert.assertEquals(AnswerMateRequestDomainResult::class, result::class)
            Assert.assertTrue(result.isSuccessful())
        }
    }
}