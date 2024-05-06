package com.qubacy.geoqq.domain.mate.requests.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer.Companion.DEFAULT_DATA_REQUEST
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsDataResult
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase._test.mock.InterlocutorUseCaseMockContainer
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.request.model.toMateRequest
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._test.mock.MateRequestUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.requests.usecase.result.get.GetRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase.result.update.UpdateRequestChunkDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class MateRequestsUseCaseTest : UseCaseTest<MateRequestsUseCase>() {
    @get:Rule
    override val rule = super.rule.around(InstantTaskExecutorRule())

    private lateinit var mMateRequestUseCaseMockContainer: MateRequestUseCaseMockContainer
    private lateinit var mInterlocutorUseCaseMockContainer: InterlocutorUseCaseMockContainer
    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer
    private lateinit var mMateRequestDataRepositoryMockContainer: MateRequestDataRepositoryMockContainer

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mMateRequestUseCaseMockContainer = MateRequestUseCaseMockContainer()
        mInterlocutorUseCaseMockContainer = InterlocutorUseCaseMockContainer()
        mLogoutUseCaseMockContainer = LogoutUseCaseMockContainer()
        mMateRequestDataRepositoryMockContainer = MateRequestDataRepositoryMockContainer()

        return superDependencies.plus(listOf(
            mMateRequestUseCaseMockContainer.mateRequestUseCaseMock,
            mInterlocutorUseCaseMockContainer.interlocutorUseCaseMock,
            mLogoutUseCaseMockContainer.logoutUseCaseMock,
            mMateRequestDataRepositoryMockContainer.mateRequestDataRepositoryMock
        ))
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MateRequestsUseCase(
            dependencies[0] as LocalErrorDataSource,
            dependencies[1] as MateRequestUseCase,
            dependencies[2] as InterlocutorUseCase,
            dependencies[3] as LogoutUseCase,
            dependencies[4] as MateRequestDataRepository
        )
    }

    @Test
    fun getRequestChunkTest() = runTest {
        val offset = 0

        val remoteGetMateRequestsDataResult = GetMateRequestsDataResult(
            false,
            listOf(DEFAULT_DATA_REQUEST, DEFAULT_DATA_REQUEST)
        )

        val expectedRemoteMateRequests = remoteGetMateRequestsDataResult.requests
            .map { it.toMateRequest() }

        mMateRequestDataRepositoryMockContainer.getMateRequestsDataResult =
            remoteGetMateRequestsDataResult

        mUseCase.resultFlow.test {
            mUseCase.getRequestChunk(offset)

            Assert.assertTrue(mMateRequestDataRepositoryMockContainer.getMateRequestsCallFlag)

            val remoteResult = awaitItem()

            Assert.assertEquals(GetRequestChunkDomainResult::class, remoteResult::class)
            Assert.assertTrue(remoteResult.isSuccessful())

            val gottenRemoteMateRequests = (remoteResult as GetRequestChunkDomainResult)
                .chunk!!.requests

            AssertUtils.assertEqualContent(expectedRemoteMateRequests, gottenRemoteMateRequests)
        }
    }

    @Test
    fun answerRequestTest() {
        val requestId = 0L
        val isAccepted = false

        mUseCase.answerRequest(requestId, isAccepted)

        Assert.assertTrue(mMateRequestUseCaseMockContainer.answerMateRequestCallFlag)
    }

    @Test
    fun getInterlocutorTest() {
        val interlocutorId = 0L

        mUseCase.getInterlocutor(interlocutorId)

        Assert.assertTrue(mInterlocutorUseCaseMockContainer.getInterlocutorCallFlag)
    }
}