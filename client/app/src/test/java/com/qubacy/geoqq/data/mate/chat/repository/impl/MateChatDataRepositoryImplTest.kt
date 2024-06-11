package com.qubacy.geoqq.data.mate.chat.repository.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.message.MessageEventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data.mate.chat.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.repository._common._test.context.MateChatDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.chat.repository._common.result.added.MateChatAddedDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.get.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.updated.MateChatUpdatedDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.LocalMateChatDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.RemoteMateChatHttpRestDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.RemoteMateChatHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.MateChatEventPayload
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.type.MateChatEventType
import com.qubacy.geoqq.data.mate.message.repository._common._test.context.MateMessageDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
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

class MateChatDataRepositoryImplTest : DataRepositoryTest<MateChatDataRepositoryImpl>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
        val DEFAULT_USER_ID_USER_MAP = UserDataRepositoryTestContext.DEFAULT_USER_ID_USER_MAP

        val DEFAULT_LAST_MESSAGE_ENTITY = MateMessageDataRepositoryTestContext.DEFAULT_MESSAGE_ENTITY
        val DEFAULT_MATE_CHAT_ENTITY = MateChatDataRepositoryTestContext.DEFAULT_MATE_CHAT_ENTITY

        val DEFAULT_GET_CHAT_RESPONSE = MateChatDataRepositoryTestContext.DEFAULT_GET_CHAT_RESPONSE
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mLocalSourceGetChats: Map<MateChatEntity, MateMessageEntity?>? = null
    private var mLocalSourceGetChatById: Map<MateChatEntity, MateMessageEntity?>? = null

    private var mLocalSourceGetChatsCallFlag = false
    private var mLocalSourceGetChatByIdCallFlag = false
    private var mLocalSourceInsertChatCallFlag = false
    private var mLocalSourceInsertChatWithLastMessageCallFlag = false
    private var mLocalSourceUpdateChatCallFlag = false
    private var mLocalSourceUpdateChatWithLastMessageCallFlag = false
    private var mLocalSourceDeleteChatCallFlag = false
    private var mLocalSourceDeleteChatsByIdsCallFlag = false
    private var mLocalSourceSaveChatsCallFlag = false
    private var mLocalSourceDeleteAllChatsCallFlag = false
    private var mLocalSourceDeleteOtherChatsByIdsCallFlag = false

    private var mRemoteHttpRestSourceGetChatResponse: GetChatResponse? = null
    private var mRemoteHttpRestSourceGetChatsResponse: GetChatsResponse? = null

    private var mRemoteHttpRestSourceGetChatCallFlag = false
    private var mRemoteHttpRestSourceGetChatsCallFlag = false

    private val mRemoteHttpWebSocketSourceEventFlow = MutableSharedFlow<WebSocketResult>()

    private var mRemoteHttpWebSocketSourceStartProducingCallFlag = false
    private var mRemoteHttpWebSocketSourceStopProducingCallFlag = false

    @Before
    fun setup() {
        initMateChatDataRepository()
    }

    @After
    fun clear() {
        mLocalSourceGetChats = null
        mLocalSourceGetChatById = null

        mLocalSourceGetChatsCallFlag = false
        mLocalSourceGetChatByIdCallFlag = false
        mLocalSourceInsertChatCallFlag = false
        mLocalSourceInsertChatWithLastMessageCallFlag = false
        mLocalSourceUpdateChatCallFlag = false
        mLocalSourceUpdateChatWithLastMessageCallFlag = false
        mLocalSourceDeleteChatCallFlag = false
        mLocalSourceDeleteChatsByIdsCallFlag = false
        mLocalSourceSaveChatsCallFlag = false
        mLocalSourceDeleteAllChatsCallFlag = false
        mLocalSourceDeleteOtherChatsByIdsCallFlag = false

        mRemoteHttpRestSourceGetChatResponse = null
        mRemoteHttpRestSourceGetChatsResponse = null

        mRemoteHttpRestSourceGetChatCallFlag = false
        mRemoteHttpRestSourceGetChatsCallFlag = false

        mRemoteHttpWebSocketSourceStartProducingCallFlag = false
        mRemoteHttpWebSocketSourceStopProducingCallFlag = false
    }

    private fun initMateChatDataRepository() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val localMateChatDataSourceMock = mockLocalMateChatDataSource()
        val remoteMateChatHttpRestDataSourceMock = mockRemoteMateChatHttpRestDataSource()
        val remoteMateChatHttpWebSocketDataSourceMock = mockRemoteMateChatHttpWebSocketDataSource()

        mDataRepository = MateChatDataRepositoryImpl(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepositoryMock,
            mLocalMateChatDatabaseDataSource = localMateChatDataSourceMock,
            mRemoteMateChatHttpRestDataSource = remoteMateChatHttpRestDataSourceMock,
            mRemoteMateChatHttpWebSocketDataSource = remoteMateChatHttpWebSocketDataSourceMock
        )
    }

    private fun mockLocalMateChatDataSource(): LocalMateChatDatabaseDataSource {
        val localMateChatDataSourceMock = Mockito.mock(LocalMateChatDatabaseDataSource::class.java)

        Mockito.`when`(localMateChatDataSourceMock.getChats(
            Mockito.anyInt(), Mockito.anyInt()
        )).thenAnswer {
            mLocalSourceGetChatsCallFlag = true
            mLocalSourceGetChats
        }
        Mockito.`when`(localMateChatDataSourceMock.getChatById(Mockito.anyLong())).thenAnswer {
            mLocalSourceGetChatByIdCallFlag = true
            mLocalSourceGetChatById
        }
        Mockito.`when`(localMateChatDataSourceMock.deleteAllChats()).thenAnswer {
            mLocalSourceDeleteAllChatsCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.deleteOtherChatsByIds(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceDeleteOtherChatsByIdsCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.insertChat(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceInsertChatCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.insertChatWithLastMessage(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceInsertChatWithLastMessageCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.updateChat(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceUpdateChatCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.updateChatWithLastMessage(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceUpdateChatWithLastMessageCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.deleteChat(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceDeleteChatCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.deleteChatsByIds(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceDeleteChatsByIdsCallFlag = true

            Unit
        }
        Mockito.`when`(localMateChatDataSourceMock.saveChats(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceSaveChatsCallFlag = true

            Unit
        }

        return localMateChatDataSourceMock
    }

    private fun mockRemoteMateChatHttpRestDataSource(): RemoteMateChatHttpRestDataSource {
        val httpMateChatDataSourceMock = Mockito.mock(RemoteMateChatHttpRestDataSource::class.java)

        Mockito.`when`(httpMateChatDataSourceMock.getChat(
            Mockito.anyLong()
        )).thenAnswer {
            mRemoteHttpRestSourceGetChatCallFlag = true
            mRemoteHttpRestSourceGetChatResponse
        }
        Mockito.`when`(httpMateChatDataSourceMock.getChats(
            Mockito.anyInt(), Mockito.anyInt()
        )).thenAnswer {
            mRemoteHttpRestSourceGetChatsCallFlag = true
            mRemoteHttpRestSourceGetChatsResponse
        }

        return httpMateChatDataSourceMock
    }

    private fun mockRemoteMateChatHttpWebSocketDataSource(): RemoteMateChatHttpWebSocketDataSource {
        val remoteMateChatHttpWebSocketDataSource =
            Mockito.mock(RemoteMateChatHttpWebSocketDataSource::class.java)

        Mockito.`when`(remoteMateChatHttpWebSocketDataSource.startProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStartProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteMateChatHttpWebSocketDataSource.stopProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStopProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteMateChatHttpWebSocketDataSource.eventFlow).thenAnswer {
            mRemoteHttpWebSocketSourceEventFlow
        }

        return remoteMateChatHttpWebSocketDataSource
    }

    @Test
    fun getChatsTest() = runTest {
        val loadedChatIds = listOf<Long>()
        val offset = 0
        val count = 5

        val remoteUserIdUserMap = DEFAULT_USER_ID_USER_MAP.apply {
            this[DEFAULT_DATA_USER.id] = DEFAULT_DATA_USER.copy(username = "updated test")
        }

        val remoteUser = remoteUserIdUserMap[DEFAULT_DATA_USER.id]!!

        val localChats = mapOf(DEFAULT_MATE_CHAT_ENTITY to DEFAULT_LAST_MESSAGE_ENTITY)
        val remoteChats = GetChatsResponse(listOf(DEFAULT_GET_CHAT_RESPONSE))

        mLocalSourceGetChats = localChats
        mRemoteHttpRestSourceGetChatsResponse = remoteChats
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUserResult =
            ResolveUsersDataResult(true, remoteUserIdUserMap)

        val expectedLocalDataChats = localChats.map {
            it.toDataMateChat(remoteUser, remoteUser)
        }
        val expectedRemoteDataChats = remoteChats.chats.map {
            it.toDataMateChat(remoteUser, remoteUser)
        }

        val getChatsResult = mDataRepository.getChats(loadedChatIds, offset, count)

        val gottenLocalResult = getChatsResult.awaitUntilVersion(0)

        Assert.assertTrue(mLocalSourceGetChatsCallFlag)

        val gottenLocalDataChats = gottenLocalResult.chats!!

        AssertUtils.assertEqualContent(expectedLocalDataChats, gottenLocalDataChats)

        val gottenRemoteResult = getChatsResult.awaitUntilVersion(1)

        Assert.assertTrue(mRemoteHttpRestSourceGetChatsCallFlag)
        Assert.assertTrue(mRemoteHttpWebSocketSourceStartProducingCallFlag)
        Assert.assertEquals(GetChatsDataResult::class, gottenRemoteResult::class)

        val gottenRemoteDataChats = gottenRemoteResult.chats!!

        AssertUtils.assertEqualContent(expectedRemoteDataChats, gottenRemoteDataChats)
    }

    @Test
    fun getChatsWithOverdueChatsTest() = runTest {
        val loadedChatIds = listOf<Long>()
        val offset = 0
        val count = 5

        val remoteUserIdUserMap = DEFAULT_USER_ID_USER_MAP.apply {
            this[DEFAULT_DATA_USER.id] = DEFAULT_DATA_USER.copy(username = "updated test")
        }

        val remoteUser = remoteUserIdUserMap[DEFAULT_DATA_USER.id]!!

        val localChats = mapOf(
            DEFAULT_MATE_CHAT_ENTITY to DEFAULT_LAST_MESSAGE_ENTITY,
            DEFAULT_MATE_CHAT_ENTITY.copy(id = 1L) to DEFAULT_LAST_MESSAGE_ENTITY.copy(id = 1L)
        )
        val httpChats = GetChatsResponse(listOf(DEFAULT_GET_CHAT_RESPONSE))

        mLocalSourceGetChats = localChats
        mRemoteHttpRestSourceGetChatsResponse = httpChats
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUserResult =
            ResolveUsersDataResult(true, remoteUserIdUserMap)

        val expectedLocalDataChats = localChats.map {
            it.toDataMateChat(remoteUser, remoteUser)
        }
        val expectedRemoteDataChats = httpChats.chats.map {
            it.toDataMateChat(remoteUser, remoteUser)
        }

        val getChatsResult = mDataRepository.getChats(loadedChatIds, offset, count)

        val gottenLocalResult = getChatsResult.awaitUntilVersion(0)

        Assert.assertTrue(mLocalSourceGetChatsCallFlag)

        val gottenLocalDataChats = gottenLocalResult.chats!!

        AssertUtils.assertEqualContent(expectedLocalDataChats, gottenLocalDataChats)

        val gottenRemoteResult = getChatsResult.awaitUntilVersion(1)

        Assert.assertTrue(mRemoteHttpRestSourceGetChatsCallFlag)
        Assert.assertTrue(mRemoteHttpWebSocketSourceStartProducingCallFlag)
        Assert.assertTrue(mLocalSourceDeleteOtherChatsByIdsCallFlag)

        val gottenRemoteDataChats = gottenRemoteResult.chats!!

        AssertUtils.assertEqualContent(expectedRemoteDataChats, gottenRemoteDataChats)
    }

    @Test
    fun processMateChatAddedEventPayloadTest() = runTest {
        val messagePayload = MessageEventPayload(0L, String(), 0L, 0L)
        val chatPayload = MateChatEventPayload(
            0L, 0L, 0, messagePayload, 0L)
        val webSocketResult = WebSocketPayloadResult(
            MateChatEventType.MATE_CHAT_ADDED_EVENT_TYPE_NAME.title,
            chatPayload
        )

        mDataRepository.resultFlow.test {
            mRemoteHttpWebSocketSourceEventFlow.emit(webSocketResult)

            val result = awaitItem()

            Assert.assertEquals(MateChatAddedDataResult::class, result::class)
            Assert.assertTrue(mLocalSourceSaveChatsCallFlag)
        }
    }

    @Test
    fun processMateChatUpdatedEventPayloadTest() = runTest {
        val messagePayload = MessageEventPayload(0L, String(), 0L, 0L)
        val chatPayload = MateChatEventPayload(
            0L, 0L, 0, messagePayload, 0L)
        val webSocketResult = WebSocketPayloadResult(
            MateChatEventType.MATE_CHAT_UPDATED_EVENT_TYPE_NAME.title,
            chatPayload
        )

        mDataRepository.resultFlow.test {
            mRemoteHttpWebSocketSourceEventFlow.emit(webSocketResult)

            val result = awaitItem()

            Assert.assertEquals(MateChatUpdatedDataResult::class, result::class)
            Assert.assertTrue(mLocalSourceSaveChatsCallFlag)
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