package com.qubacy.geoqq.data.mate.message.repository.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data.mate.message.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository._common._test.context.MateMessageDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.message.repository._common.result.get.GetMessagesDataResult
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._common.result.resolve.ResolveUsersDataResult
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MateMessageDataRepositoryImplTest : DataRepositoryTest<MateMessageDataRepositoryImpl>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
        val DEFAULT_USER_ID_USER_MAP = UserDataRepositoryTestContext.DEFAULT_USER_ID_USER_MAP

        val DEFAULT_MESSAGE_ENTITY = MateMessageDataRepositoryTestContext.DEFAULT_MESSAGE_ENTITY
        val DEFAULT_GET_MESSAGE_RESPONSE = MateMessageDataRepositoryTestContext
            .DEFAULT_GET_MESSAGE_RESPONSE
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

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

    private var mHttpSourceGetMateMessagesResponse: GetMessagesResponse? = null

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

        mHttpSourceGetMateMessagesResponse = null

        mHttpSourceGetMateMessagesCallFlag = false
    }

    private fun initMateMessageRepository() = runTest {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val localMateMessageDataSourceMock = mockLocalMateMessageDataSource()
        val httpMateMessageDataSourceMock = mockHttpMateMessageDataSource()

        mDataRepository = MateMessageDataRepositoryImpl(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mLocalMateMessageDatabaseDataSource = localMateMessageDataSourceMock,
            mRemoteMateMessageHttpRestDataSource = httpMateMessageDataSourceMock
        )
    }

    private fun mockLocalMateMessageDataSource(): LocalMateMessageDatabaseDataSource {
        val localMateMessageDataSourceMock = Mockito.mock(LocalMateMessageDatabaseDataSource::class.java)

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

    private fun mockHttpMateMessageDataSource(): RemoteMateMessageHttpRestDataSource {
        val httpMateMessageDataSourceMock = Mockito.mock(RemoteMateMessageHttpRestDataSource::class.java)

        Mockito.`when`(httpMateMessageDataSourceMock.getMateMessages(
            Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()
        )).thenAnswer {
            mHttpSourceGetMateMessagesCallFlag = true
            mHttpSourceGetMateMessagesResponse
        }

        return httpMateMessageDataSourceMock
    }

    @Test
    fun getMessagesTest() = runTest {
        val chatId = 0L
        val loadedMessageIds = listOf<Long>()
        val offset = 0
        val count = 5

        val remoteUserIdUserMap = DEFAULT_USER_ID_USER_MAP.apply {
            for (entry in this) this[entry.key] = entry.value.copy(username = "updated test")
        }

        val remoteUser = remoteUserIdUserMap.values.first()

        val localMessages = listOf(DEFAULT_MESSAGE_ENTITY)
        val httpMessages = GetMessagesResponse(listOf(DEFAULT_GET_MESSAGE_RESPONSE))

        mLocalSourceGetMateMessages = localMessages
        mHttpSourceGetMateMessagesResponse = httpMessages
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUserResult =
            ResolveUsersDataResult(true, remoteUserIdUserMap)

        val expectedLocalDataMessages = localMessages.map { it.toDataMessage(remoteUser) }
        val expectedRemoteDataMessages = httpMessages.messages.map { it.toDataMessage(remoteUser) }

        val getMessagesResult = mDataRepository.getMessages(chatId, loadedMessageIds, offset, count)

        val gottenLocalResult = getMessagesResult.awaitUntilVersion(0)

        Assert.assertTrue(mLocalSourceGetMateMessagesCallFlag)

        val gottenLocalDataMessages = gottenLocalResult.messages!!

        AssertUtils.assertEqualContent(expectedLocalDataMessages, gottenLocalDataMessages)

        val gottenRemoteResult = getMessagesResult.awaitUntilVersion(1)

        Assert.assertTrue(mHttpSourceGetMateMessagesCallFlag)
        Assert.assertEquals(GetMessagesDataResult::class, gottenRemoteResult::class)

        val gottenRemoteDataMessages = gottenRemoteResult.messages!!

        AssertUtils.assertEqualContent(expectedRemoteDataMessages, gottenRemoteDataMessages)
    }

    @Test
    fun getMessagesWithOverdueMessagesTest() = runTest {
        val chatId = 0L
        val loadedMessageIds = listOf<Long>()
        val offset = 0
        val count = 5

        val remoteUserIdUserMap = DEFAULT_USER_ID_USER_MAP.apply {
            for (entry in this) this[entry.key] = entry.value.copy(username = "updated test")
        }

        val remoteUser = remoteUserIdUserMap.values.first()

        val localMessages = listOf(DEFAULT_MESSAGE_ENTITY, DEFAULT_MESSAGE_ENTITY.copy(id = 1L))
        val httpMessages = GetMessagesResponse(listOf(DEFAULT_GET_MESSAGE_RESPONSE))

        mLocalSourceGetMateMessages = localMessages
        mHttpSourceGetMateMessagesResponse = httpMessages
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUserResult =
            ResolveUsersDataResult(true, remoteUserIdUserMap)

        val expectedLocalDataMessages = localMessages.map { it.toDataMessage(remoteUser) }
        val expectedRemoteDataMessages = httpMessages.messages.map { it.toDataMessage(remoteUser) }

        val getMessagesResult = mDataRepository.getMessages(chatId, loadedMessageIds, offset, count)

        val gottenLocalResult = getMessagesResult.awaitUntilVersion(0)

        Assert.assertTrue(mLocalSourceGetMateMessagesCallFlag)

        val gottenLocalDataMessages = gottenLocalResult.messages!!

        AssertUtils.assertEqualContent(expectedLocalDataMessages, gottenLocalDataMessages)

        val gottenRemoteResult = getMessagesResult.awaitUntilVersion(1)

        Assert.assertTrue(mHttpSourceGetMateMessagesCallFlag)
        Assert.assertTrue(mLocalSourceDeleteOtherMessagesByIdsCallFlag)

        val gottenRemoteDataMessages = gottenRemoteResult.messages!!

        AssertUtils.assertEqualContent(expectedRemoteDataMessages, gottenRemoteDataMessages)
    }
}