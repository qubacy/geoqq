package com.qubacy.geoqq.domain.mate.requests.usecase

import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer.Companion.DEFAULT_DATA_REQUEST
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsDataResult
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase._test.mock.InterlocutorUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.request.model.toMateRequest
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._test.mock.MateRequestUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.requests.usecase.result.GetRequestChunkDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MateRequestsUseCaseTest : UseCaseTest<MateRequestsUseCase>() {
    private lateinit var mMateRequestUseCaseMockContainer: MateRequestUseCaseMockContainer
    private lateinit var mInterlocutorUseCaseMockContainer: InterlocutorUseCaseMockContainer
    private lateinit var mMateRequestDataRepositoryMockContainer: MateRequestDataRepositoryMockContainer

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mMateRequestUseCaseMockContainer = MateRequestUseCaseMockContainer()
        mInterlocutorUseCaseMockContainer = InterlocutorUseCaseMockContainer()
        mMateRequestDataRepositoryMockContainer = MateRequestDataRepositoryMockContainer()

        return superDependencies.plus(listOf(
            mMateRequestUseCaseMockContainer.mateRequestUseCaseMock,
            mInterlocutorUseCaseMockContainer.interlocutorUseCaseMock,
            mMateRequestDataRepositoryMockContainer.mateRequestDataRepositoryMock
        ))
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MateRequestsUseCase(
            dependencies[0] as ErrorDataRepository,
            dependencies[1] as MateRequestUseCase,
            dependencies[2] as InterlocutorUseCase,
            dependencies[3] as MateRequestDataRepository
        )
    }

    @Test
    fun getRequestChunkTest() = runTest {
        val offset = 0
        val getMateRequestsDataResult = GetMateRequestsDataResult(listOf(
            DEFAULT_DATA_REQUEST,
            DEFAULT_DATA_REQUEST,
        ))

        val expectedMateRequests = getMateRequestsDataResult.requests.map { it.toMateRequest() }

        mMateRequestDataRepositoryMockContainer.getMateRequestsDataResult = getMateRequestsDataResult

        mUseCase.resultFlow.test {
            mUseCase.getRequestChunk(offset)

            val result = awaitItem()

            Assert.assertTrue(mMateRequestDataRepositoryMockContainer.getMateRequestsCallFlag)
            Assert.assertEquals(GetRequestChunkDomainResult::class, result::class)
            Assert.assertTrue(result.isSuccessful())

            val gottenMateRequests = (result as GetRequestChunkDomainResult).chunk!!.requests

            AssertUtils.assertEqualContent(expectedMateRequests, gottenMateRequests)
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