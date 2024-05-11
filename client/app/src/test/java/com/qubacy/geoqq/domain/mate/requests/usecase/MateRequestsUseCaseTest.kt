package com.qubacy.geoqq.domain.mate.requests.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.request.repository._test.mock.MateRequestDataRepositoryMockContainer.Companion.DEFAULT_DATA_REQUEST
import com.qubacy.geoqq.data.mate.request.repository._common.result.GetMateRequestsDataResult
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.interlocutor.usecase.impl.InterlocutorUseCaseImpl
import com.qubacy.geoqq.domain.interlocutor.usecase._test.mock.InterlocutorUseCaseMockContainer
import com.qubacy.geoqq.domain.logout.usecase.impl.LogoutUseCaseImpl
import com.qubacy.geoqq.domain.logout.usecase._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.request.model.toMateRequest
import com.qubacy.geoqq.domain.mate.request.usecase.impl.MateRequestUseCaseImpl
import com.qubacy.geoqq.domain.mate.request.usecase._test.mock.MateRequestUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.get.GetRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase.impl.MateRequestsUseCaseImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class MateRequestsUseCaseTest : UseCaseTest<MateRequestsUseCaseImpl>() {
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
        mUseCase = MateRequestsUseCaseImpl(
            dependencies[0] as LocalErrorDatabaseDataSourceImpl,
            dependencies[1] as MateRequestUseCaseImpl,
            dependencies[2] as InterlocutorUseCaseImpl,
            dependencies[3] as LogoutUseCaseImpl,
            dependencies[4] as MateRequestDataRepositoryImpl
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