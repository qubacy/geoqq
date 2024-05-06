package com.qubacy.geoqq.domain.mate.request.usecase

import app.cash.turbine.test
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCaseTest
import com.qubacy.geoqq.domain.mate.request.usecase.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.SendMateRequestDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MateRequestUseCaseTest : UseCaseTest<MateRequestUseCase>() {
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
        mUseCase = MateRequestUseCase(
            dependencies[0] as LocalErrorDataSource,
            dependencies[1] as LogoutUseCase,
            dependencies[2] as MateRequestDataRepository
        )
    }

    @Test
    fun sendMateRequestTest() = runTest {
        val interlocutor = MateChatUseCaseTest.DEFAULT_DATA_USER

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