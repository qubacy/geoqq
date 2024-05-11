package com.qubacy.geoqq.domain.mate.request.usecase

import app.cash.turbine.test
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.logout.usecase.impl.LogoutUseCaseImpl
import com.qubacy.geoqq.domain.logout.usecase._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCaseTest
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.impl.MateRequestUseCaseImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MateRequestUseCaseTest : UseCaseTest<MateRequestUseCaseImpl>() {
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
            dependencies[0] as LocalErrorDatabaseDataSourceImpl,
            dependencies[1] as LogoutUseCaseImpl,
            dependencies[2] as MateRequestDataRepositoryImpl
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