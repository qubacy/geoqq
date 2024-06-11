package com.qubacy.geoqq.data.mate.request.repository.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository._common._test.context.MateRequestDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.request.repository._common.result.added.MateRequestAddedDataResult
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.RemoteMateRequestHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.MateRequestAddedEventPayload
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.type.MateRequestEventType
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._common.result.resolve.ResolveUsersDataResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MateRequestDataRepositoryImplTest : DataRepositoryTest<MateRequestDataRepositoryImpl>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
        val DEFAULT_USER_ID_USER_MAP = UserDataRepositoryTestContext.DEFAULT_USER_ID_USER_MAP
        val DEFAULT_GET_MATE_REQUEST_RESPONSE = MateRequestDataRepositoryTestContext
            .DEFAULT_GET_MATE_REQUEST_RESPONSE
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mHttpSourceGetMateRequestsResponse: GetMateRequestsResponse? = null
    private var mHttpSourceGetMateRequestCountResponse: GetMateRequestCountResponse? = null

    private var mRemoteHttpRestSourceGetMateRequestsCallFlag = false
    private var mRemoteHttpRestSourceGetMateRequestCountCallFlag = false
    private var mRemoteHttpRestSourcePostMateRequestCallFlag = false
    private var mRemoteHttpRestSourceAnswerMateRequestCallFlag = false

    private val mRemoteHttpWebSocketSourceEventFlow = MutableSharedFlow<WebSocketResult>()

    private var mRemoteHttpWebSocketSourceStartProducingCallFlag = false
    private var mRemoteHttpWebSocketSourceStopProducingCallFlag = false

    @Before
    fun setup() {
        initMateRequestDataRepository()
    }

    @After
    fun clear() {
        mHttpSourceGetMateRequestsResponse = null
        mHttpSourceGetMateRequestCountResponse = null

        mRemoteHttpRestSourceGetMateRequestsCallFlag = false
        mRemoteHttpRestSourceGetMateRequestCountCallFlag = false
        mRemoteHttpRestSourcePostMateRequestCallFlag = false
        mRemoteHttpRestSourceAnswerMateRequestCallFlag = false

        mRemoteHttpWebSocketSourceStartProducingCallFlag = false
        mRemoteHttpWebSocketSourceStopProducingCallFlag = false
    }

    private fun initMateRequestDataRepository() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val remoteMateRequestHttpRestDataSourceMock = mockRemoteMateRequestHttpRestDataSource()
        val remoteMateRequestHttpWebSocketDataSourceMock =
            mockRemoteMateRequestHttpWebSocketDataSource()

        mDataRepository = MateRequestDataRepositoryImpl(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepositoryMock,
            mRemoteMateRequestHttpRestDataSource = remoteMateRequestHttpRestDataSourceMock,
            mRemoteMateRequestHttpWebSocketDataSource = remoteMateRequestHttpWebSocketDataSourceMock
        )
    }

    private fun mockRemoteMateRequestHttpRestDataSource(): RemoteMateRequestHttpRestDataSource {
        val httpMateRequestDataSourceMock = Mockito.mock(RemoteMateRequestHttpRestDataSource::class.java)

        Mockito.`when`(httpMateRequestDataSourceMock.getMateRequests(
            Mockito.anyInt(),
            Mockito.anyInt()
        )).thenAnswer {
            mRemoteHttpRestSourceGetMateRequestsCallFlag = true
            mHttpSourceGetMateRequestsResponse
        }
        Mockito.`when`(httpMateRequestDataSourceMock.getMateRequestCount()).thenAnswer {
            mRemoteHttpRestSourceGetMateRequestCountCallFlag = true
            mHttpSourceGetMateRequestCountResponse
        }
        Mockito.`when`(httpMateRequestDataSourceMock.postMateRequest(
            Mockito.anyLong()
        )).thenAnswer {
            mRemoteHttpRestSourcePostMateRequestCallFlag = true

            Unit
        }
        Mockito.`when`(httpMateRequestDataSourceMock.answerMateRequest(
            Mockito.anyLong(),
            Mockito.anyBoolean()
        )).thenAnswer {
            mRemoteHttpRestSourceAnswerMateRequestCallFlag = true

            Unit
        }

        return httpMateRequestDataSourceMock
    }

    private fun mockRemoteMateRequestHttpWebSocketDataSource(

    ): RemoteMateRequestHttpWebSocketDataSource {
        val remoteMateRequestHttpWebSocketDataSourceMock =
            Mockito.mock(RemoteMateRequestHttpWebSocketDataSource::class.java)

        Mockito.`when`(remoteMateRequestHttpWebSocketDataSourceMock.startProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStartProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteMateRequestHttpWebSocketDataSourceMock.stopProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStopProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteMateRequestHttpWebSocketDataSourceMock.eventFlow).thenAnswer {
            mRemoteHttpWebSocketSourceEventFlow
        }

        return remoteMateRequestHttpWebSocketDataSourceMock
    }

    @Test
    fun getMateRequestsTest() = runTest {
        val offset = 0
        val count = 5
        val getMateRequestsResponse = GetMateRequestsResponse(listOf(
            DEFAULT_GET_MATE_REQUEST_RESPONSE
        ))

        val userIdUserMap = DEFAULT_USER_ID_USER_MAP

        val remoteUser = userIdUserMap[DEFAULT_DATA_USER.id]!!

        val expectedRemoteDataMateRequests = getMateRequestsResponse.requests.map {
            it.toDataMateRequest(remoteUser)
        }

        mHttpSourceGetMateRequestsResponse = getMateRequestsResponse
        mUserDataRepositoryMockContainer.resolveUsersResult =
            ResolveUsersDataResult(true, userIdUserMap)

        val getMateRequestsResult = mDataRepository.getMateRequests(offset, count)

        val gottenRemoteDataMateRequests = getMateRequestsResult.awaitUntilVersion(0).requests

        Assert.assertTrue(mRemoteHttpRestSourceGetMateRequestsCallFlag)
        Assert.assertTrue(mRemoteHttpWebSocketSourceStartProducingCallFlag)
        Assert.assertTrue(mUserDataRepositoryMockContainer.resolveUsersCallFlag)

        AssertUtils.assertEqualContent(expectedRemoteDataMateRequests, gottenRemoteDataMateRequests)
    }

    @Test
    fun getMateRequestCountTest() = runTest {
        val getMateRequestCountResponse = GetMateRequestCountResponse(5)

        val expectedMateRequestCount = getMateRequestCountResponse.count

        mHttpSourceGetMateRequestCountResponse = getMateRequestCountResponse

        val getMateRequestCountResult = mDataRepository.getMateRequestCount()
        val gottenDataMateRequestCount = getMateRequestCountResult.count

        Assert.assertTrue(mRemoteHttpRestSourceGetMateRequestCountCallFlag)
        Assert.assertEquals(expectedMateRequestCount, gottenDataMateRequestCount)
    }

    @Test
    fun createMateRequestTest() = runTest {
        val userId = DEFAULT_DATA_USER.id

        mDataRepository.createMateRequest(userId)

        Assert.assertTrue(mRemoteHttpRestSourcePostMateRequestCallFlag)
    }

    @Test
    fun answerMateRequestTest() = runTest {
        val id = 0L
        val isAccepted = true

        mDataRepository.answerMateRequest(id, isAccepted)

        Assert.assertTrue(mRemoteHttpRestSourceAnswerMateRequestCallFlag)
    }

    @Test
    fun processMateRequestAddedEventPayloadTest() = runTest {
        val payload = MateRequestAddedEventPayload(0L, 0L)
        val webSocketResult = WebSocketPayloadResult(
            MateRequestEventType.MATE_REQUEST_ADDED_EVENT_TYPE.title,
            payload
        )

        mDataRepository.resultFlow.test {
            mRemoteHttpWebSocketSourceEventFlow.emit(webSocketResult)

            val result = awaitItem()

            Assert.assertEquals(MateRequestAddedDataResult::class, result::class)
            Assert.assertTrue(mUserDataRepositoryMockContainer.getUsersByIdsCallFlag)
        }
    }

    @Test
    fun processWebSocketErrorResultTest() = runTest {
        val error = TestError.normal
        val webSocketErrorResult = WebSocketErrorResult(error)

        val expectedException = ErrorAppException(error)

        mDataRepository.resultFlow.test {
            mRemoteHttpWebSocketSourceEventFlow.emit(webSocketErrorResult)

            val gottenException = awaitError()

            Assert.assertEquals(expectedException, gottenException)
        }
    }
}