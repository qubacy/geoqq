package com.qubacy.geoqq.data.mate.message.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.message.GetMessageResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.message.GetMessagesResponse
import com.qubacy.geoqq.data._common.repository.source.remote.http.executor.mock.HttpCallExecutorMockContainer
import com.qubacy.geoqq.data.error.repository._test.mock.ErrorDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.message.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesDataResult
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito
import retrofit2.Call

class MateMessageDataRepositoryTest : DataRepositoryTest<MateMessageDataRepository>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryMockContainer.DEFAULT_DATA_USER
        val DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER = mapOf(DEFAULT_DATA_USER.id to DEFAULT_DATA_USER)

        val DEFAULT_MESSAGE_ENTITY = MateMessageEntity(
            0, 0, DEFAULT_DATA_USER.id, "local message", 0)
        val DEFAULT_GET_MESSAGE_RESPONSE =
            GetMessageResponse(0, DEFAULT_DATA_USER.id, "remote one", 0)
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer
    private lateinit var mHttpCallExecutorMockContainer: HttpCallExecutorMockContainer

    private var mLocalSourceGetMateMessages: List<MateMessageEntity> = listOf()
    private var mLocalSourceGetMateMessage: MateMessageEntity? = null

    private var mLocalSourceGetMateMessagesCallFlag = false
    private var mLocalSourceGetMateMessageCallFlag = false
    private var mLocalSourceInsertMateMessageCallFlag = false
    private var mLocalSourceUpdateMateMessageCallFlag = false
    private var mLocalSourceDeleteMateMessageCallFlag = false
    private var mLocalSourceDeleteMateMessagesByIdsCallFlag = false
    private var mLocalSourceSaveMateMessageCallFlag = false
    private var mLocalSourceDeleteOtherMessagesByIdsCallFlag = false
    private var mLocalSourceDeleteAllMessagesCallFlag = false

    private var mHttpSourceGetMateMessagesCallFlag = false

    @Before
    fun setup() {
        initMateMessageRepository()
    }

    @After
    fun clear() {
        mLocalSourceGetMateMessages = listOf()
        mLocalSourceGetMateMessage = null

        mLocalSourceGetMateMessagesCallFlag = false
        mLocalSourceGetMateMessageCallFlag = false
        mLocalSourceInsertMateMessageCallFlag = false
        mLocalSourceUpdateMateMessageCallFlag = false
        mLocalSourceDeleteMateMessageCallFlag = false
        mLocalSourceDeleteMateMessagesByIdsCallFlag = false
        mLocalSourceSaveMateMessageCallFlag = false
        mLocalSourceDeleteOtherMessagesByIdsCallFlag = false
        mLocalSourceDeleteAllMessagesCallFlag = false

        mHttpSourceGetMateMessagesCallFlag = false
    }

    private fun initMateMessageRepository() = runTest {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()
        mHttpCallExecutorMockContainer = HttpCallExecutorMockContainer()

        val localMateMessageDataSourceMock = mockLocalMateMessageDataSource()
        val httpMateMessageDataSourceMock = mockHttpMateMessageDataSource()

        mDataRepository = MateMessageDataRepository(
            mErrorDataRepository = mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepositoryMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mLocalMateMessageDataSource = localMateMessageDataSourceMock,
            mHttpMateMessageDataSource = httpMateMessageDataSourceMock,
            mHttpCallExecutor = mHttpCallExecutorMockContainer.httpCallExecutor
        )
    }

    private fun mockLocalMateMessageDataSource(): LocalMateMessageDataSource {
        val localMateMessageDataSourceMock = Mockito.mock(LocalMateMessageDataSource::class.java)

        Mockito.`when`(localMateMessageDataSourceMock.getMessages(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()
        )).thenAnswer {
            mLocalSourceGetMateMessagesCallFlag = true
            mLocalSourceGetMateMessages
        }
        Mockito.`when`(localMateMessageDataSourceMock.getMessage(
            Mockito.anyLong(), Mockito.anyLong()
        )).thenAnswer {
            mLocalSourceGetMateMessageCallFlag = true
            mLocalSourceGetMateMessage
        }
        Mockito.`when`(localMateMessageDataSourceMock.insertMessage(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceInsertMateMessageCallFlag = true

            Unit
        }
        Mockito.`when`(localMateMessageDataSourceMock.updateMessage(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceUpdateMateMessageCallFlag = true

            Unit
        }
        Mockito.`when`(localMateMessageDataSourceMock.deleteMessage(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceDeleteMateMessageCallFlag = true

            Unit
        }
        Mockito.`when`(localMateMessageDataSourceMock.deleteMessagesByIds(
            Mockito.anyLong(),
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceDeleteMateMessagesByIdsCallFlag = true

            Unit
        }
        Mockito.`when`(localMateMessageDataSourceMock.deleteOtherMessagesByIds(
            Mockito.anyLong(), AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceDeleteOtherMessagesByIdsCallFlag = true

            Unit
        }
        Mockito.`when`(localMateMessageDataSourceMock.deleteAllMessages(
            Mockito.anyLong()
        )).thenAnswer {
            mLocalSourceDeleteAllMessagesCallFlag = true

            Unit
        }
        Mockito.`when`(localMateMessageDataSourceMock.saveMessages(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceSaveMateMessageCallFlag = true

            Unit
        }

        return localMateMessageDataSourceMock
    }

    private fun mockHttpMateMessageDataSource(): HttpMateMessageDataSource {
        val getMateMessagesCallMock = Mockito.mock(Call::class.java)

        val httpMateMessageDataSourceMock = Mockito.mock(HttpMateMessageDataSource::class.java)

        Mockito.`when`(httpMateMessageDataSourceMock.getMateMessages(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceGetMateMessagesCallFlag = true
            getMateMessagesCallMock
        }

        return httpMateMessageDataSourceMock
    }

    @Test
    fun getMessagesTest() = runTest {
        val chatId = 0L
        val loadedMessageIds = listOf<Long>()
        val offset = 0
        val count = 5

        val resolveUsersWithLocalUser = DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER
        val user = DEFAULT_DATA_USER
        val localMessages = listOf(DEFAULT_MESSAGE_ENTITY)
        val httpMessages = GetMessagesResponse(listOf(DEFAULT_GET_MESSAGE_RESPONSE))

        mLocalSourceGetMateMessages = localMessages
        mHttpCallExecutorMockContainer.response = httpMessages
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUser = resolveUsersWithLocalUser

        val expectedLocalDataMessages = localMessages.map { it.toDataMessage(user) }
        val expectedHttpDataMessages = httpMessages.messages.map { it.toDataMessage(user) }

        mDataRepository.resultFlow.test {
            val gottenLocalResult = mDataRepository.getMessages(chatId, loadedMessageIds, offset, count).await()

            Assert.assertTrue(mLocalSourceGetMateMessagesCallFlag)

            val gottenLocalDataMessages = gottenLocalResult!!.messages

            AssertUtils.assertEqualContent(expectedLocalDataMessages, gottenLocalDataMessages)

            Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)

            val gottenHttpResult = awaitItem()

            Assert.assertTrue(mHttpSourceGetMateMessagesCallFlag)
            Assert.assertTrue(mHttpCallExecutorMockContainer.executeNetworkRequestCallFlag)
            Assert.assertEquals(GetMessagesDataResult::class, gottenHttpResult::class)

            val gottenHttpDataMessages = (gottenHttpResult as GetMessagesDataResult).messages

            AssertUtils.assertEqualContent(expectedHttpDataMessages, gottenHttpDataMessages)
        }
    }

    @Test
    fun getMessagesWithOverdueMessagesTest() = runTest {
        val chatId = 0L
        val loadedMessageIds = listOf<Long>()
        val offset = 0
        val count = 5

        val resolveUsersWithLocalUser = DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER
        val user = DEFAULT_DATA_USER
        val localMessages = listOf(DEFAULT_MESSAGE_ENTITY, DEFAULT_MESSAGE_ENTITY.copy(id = 1L))
        val httpMessages = GetMessagesResponse(listOf(DEFAULT_GET_MESSAGE_RESPONSE))

        mLocalSourceGetMateMessages = localMessages
        mHttpCallExecutorMockContainer.response = httpMessages
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUser = resolveUsersWithLocalUser

        val expectedLocalDataMessages = localMessages.map { it.toDataMessage(user) }
        val expectedHttpDataMessages = httpMessages.messages.map { it.toDataMessage(user) }

        mDataRepository.resultFlow.test {
            val gottenLocalResult = mDataRepository
                .getMessages(chatId, loadedMessageIds, offset, count).await()

            Assert.assertTrue(mLocalSourceGetMateMessagesCallFlag)

            val gottenLocalDataMessages = gottenLocalResult!!.messages

            AssertUtils.assertEqualContent(expectedLocalDataMessages, gottenLocalDataMessages)

            Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)

            val gottenHttpResult = awaitItem()

            Assert.assertTrue(mHttpSourceGetMateMessagesCallFlag)
            Assert.assertTrue(mHttpCallExecutorMockContainer.executeNetworkRequestCallFlag)
            Assert.assertTrue(mLocalSourceDeleteOtherMessagesByIdsCallFlag)
            Assert.assertEquals(GetMessagesDataResult::class, gottenHttpResult::class)

            val gottenHttpDataMessages = (gottenHttpResult as GetMessagesDataResult).messages

            AssertUtils.assertEqualContent(expectedHttpDataMessages, gottenHttpDataMessages)
        }
    }
}