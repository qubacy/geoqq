package com.qubacy.geoqq.data.geo.message.repository.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessageResponse
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.geo.message.repository._common.result.added.GeoMessageAddedDataResult
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.RemoteGeoMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.GeoMessageAddedEventPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.type.GeoMessageEventType
import com.qubacy.geoqq.data.geo.message.repository.impl._common._test.context.GeoMessageDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class GeoMessageDataRepositoryImplTest : DataRepositoryTest<GeoMessageDataRepositoryImpl>() {
    companion object {
        val DEFAULT_GET_MESSAGE_RESPONSE = GeoMessageDataRepositoryTestContext
            .DEFAULT_GET_MESSAGE_RESPONSE
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(MainDispatcherRule())
        .around(InstantTaskExecutorRule())

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mHttpSourceGetMessagesResponse: GetMessagesResponse? = null

    private var mRemoteHttpRestSourceGetMessagesCallFlag = false

    private val mRemoteHttpWebSocketSourceEventFlow = MutableSharedFlow<WebSocketResult>()

    private var mRemoteHttpWebSocketSourceStartProducingCallFlag = false
    private var mRemoteHttpWebSocketSourceStopProducingCallFlag = false
    private var mRemoteHttpWebSocketSourceSendLocationCallFlag = false
    private var mRemoteHttpWebSocketSourceSendMessageCallFlag = false

    @Before
    fun setup() {
        mDataRepository = initGeoMessageDataRepository()
    }

    @After
    fun clear() {
        mHttpSourceGetMessagesResponse = null

        mRemoteHttpRestSourceGetMessagesCallFlag = false

        mRemoteHttpWebSocketSourceStartProducingCallFlag = false
        mRemoteHttpWebSocketSourceStopProducingCallFlag = false
        mRemoteHttpWebSocketSourceSendLocationCallFlag = false
        mRemoteHttpWebSocketSourceSendMessageCallFlag = false
    }

    private fun initGeoMessageDataRepository(): GeoMessageDataRepositoryImpl {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val remoteGeoMessageHttpRestDataSourceMock = mockRemoteGeoMessageHttpRestDataSource()
        val remoteGeoMessageHttpWebSocketDataSourceMock = mockRemoteGeoMessageHttpWebSocketDataSource()

        return GeoMessageDataRepositoryImpl(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepositoryMock,
            mRemoteGeoMessageHttpRestDataSource = remoteGeoMessageHttpRestDataSourceMock,
            mRemoteGeoMessageHttpWebSocketDataSource = remoteGeoMessageHttpWebSocketDataSourceMock
        )
    }

    private fun mockRemoteGeoMessageHttpRestDataSource(): RemoteGeoMessageHttpRestDataSource {
        val httpGeoMessageDataSourceMock = Mockito.mock(RemoteGeoMessageHttpRestDataSource::class.java)

        Mockito.`when`(httpGeoMessageDataSourceMock.getMessages(
            Mockito.anyInt(), Mockito.anyFloat(), Mockito.anyFloat()
        )).thenAnswer {
            mRemoteHttpRestSourceGetMessagesCallFlag = true
            mHttpSourceGetMessagesResponse
        }

        return httpGeoMessageDataSourceMock
    }

    private fun mockRemoteGeoMessageHttpWebSocketDataSource(

    ): RemoteGeoMessageHttpWebSocketDataSource {
        val remoteGeoMessageHttpRestDataSource =
            Mockito.mock(RemoteGeoMessageHttpWebSocketDataSource::class.java)

        Mockito.`when`(remoteGeoMessageHttpRestDataSource.startProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStartProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteGeoMessageHttpRestDataSource.stopProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStopProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteGeoMessageHttpRestDataSource.sendMessage(
            Mockito.anyString(), Mockito.anyFloat(), Mockito.anyFloat()
        )).thenAnswer {
            mRemoteHttpWebSocketSourceSendMessageCallFlag = true

            Unit
        }
        Mockito.`when`(remoteGeoMessageHttpRestDataSource.sendLocation(
            Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyInt()
        )).thenAnswer {
            mRemoteHttpWebSocketSourceSendLocationCallFlag = true

            Unit
        }
        Mockito.`when`(remoteGeoMessageHttpRestDataSource.eventFlow).thenAnswer {
            mRemoteHttpWebSocketSourceEventFlow
        }

        return remoteGeoMessageHttpRestDataSource
    }

    @Test
    fun getMessagesTest() = runTest {
        val radius = 0
        val longitude = 0f
        val latitude = 0f
        val userId = 0L

        val resolveUsersResult = UserDataRepositoryMockContainer.DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER
        val getMessagesResponse = generateGetMessagesResponse(2)

        val dataUser = resolveUsersResult.userIdUserMap[userId]!!

        val expectedDataMessages = getMessagesResponse.messages.map { it.toDataMessage(dataUser) }

        mHttpSourceGetMessagesResponse = getMessagesResponse
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUserResult = resolveUsersResult

        val getMessagesResultLiveData = mDataRepository.getMessages(radius, longitude, latitude)
        val getMessagesResult =getMessagesResultLiveData.await()

        val gottenDatMessages = getMessagesResult.messages

        Assert.assertTrue(mUserDataRepositoryMockContainer.resolveUsersWithLocalUserCallFlag)
        Assert.assertTrue(mRemoteHttpRestSourceGetMessagesCallFlag)
        Assert.assertTrue(mRemoteHttpWebSocketSourceStartProducingCallFlag)
        AssertUtils.assertEqualContent(expectedDataMessages, gottenDatMessages)
    }

    @Test
    fun sendMessageTest() = runTest {
        val text = "test"
        val longitude = 0f
        val latitude = 0f

        mDataRepository.sendMessage(text, longitude, latitude)

        Assert.assertTrue(mRemoteHttpWebSocketSourceSendMessageCallFlag)
    }

    @Test
    fun processGeoMessageAddedEventPayloadTest() = runTest {
        val payload = GeoMessageAddedEventPayload(0L, String(), 0L, 0L)
        val webSocketEvent = WebSocketPayloadResult(
            GeoMessageEventType.GEO_MESSAGE_ADDED_EVENT_TYPE.title,
            payload
        )

        mDataRepository.resultFlow.test {
            mRemoteHttpWebSocketSourceEventFlow.emit(webSocketEvent)

            val result = awaitItem()

            Assert.assertEquals(GeoMessageAddedDataResult::class, result::class)
        }
    }

    private fun generateGetMessagesResponse(
        count: Int,
        userId: Long = DEFAULT_GET_MESSAGE_RESPONSE.userId
    ): GetMessagesResponse {
        return GetMessagesResponse(
            IntRange(0, count - 1).reversed().map {
                val id = it.toLong()
                val time = count - 1 - it.toLong()

                GetMessageResponse(id, userId, "test $id", time)
            }
        )
    }
}