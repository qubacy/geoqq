package com.qubacy.geoqq.data.mate.message.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message.GetMessageResponse
import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message.GetMessagesResponse
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
import retrofit2.Response

class MateMessageDataRepositoryTest : DataRepositoryTest<MateMessageDataRepository>() {
    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mLocalSourceGetMateMessages: List<MateMessageEntity> = listOf()
    private var mLocalSourceGetMateMessage: MateMessageEntity? = null

    private var mLocalSourceGetMateMessagesCallFlag = false
    private var mLocalSourceGetMateMessageCallFlag = false
    private var mLocalSourceInsertMateMessageCallFlag = false
    private var mLocalSourceUpdateMateMessageCallFlag = false
    private var mLocalSourceDeleteMateMessageCallFlag = false
    private var mLocalSourceSaveMateMessageCallFlag = false

    private var mHttpSourceGetMateMessagesResponse: GetMessagesResponse? = null

    private var mHttpSourceGetMateMessagesResponseCallFlag = false
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
        mLocalSourceSaveMateMessageCallFlag = false

        mHttpSourceGetMateMessagesResponse = null

        mHttpSourceGetMateMessagesResponseCallFlag = false
        mHttpSourceGetMateMessagesCallFlag = false
    }

    private fun initMateMessageRepository() = runTest {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val localMateMessageDataSourceMock = mockLocalMateMessageDataSource()
        val httpMateMessageDataSourceMock = mockHttpMateMessageDataSource()

        mDataRepository = MateMessageDataRepository(
            mErrorDataRepository = mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepositoryMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mLocalMateMessageDataSource = localMateMessageDataSourceMock,
            mHttpMateMessageDataSource = httpMateMessageDataSourceMock
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
        Mockito.`when`(localMateMessageDataSourceMock.saveMessages(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mLocalSourceSaveMateMessageCallFlag = true

            Unit
        }

        return localMateMessageDataSourceMock
    }

    private fun mockHttpMateMessageDataSource(): HttpMateMessageDataSource {
        val getMateMessagesResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(getMateMessagesResponseMock.body()).thenAnswer {
            mHttpSourceGetMateMessagesResponseCallFlag = true
            mHttpSourceGetMateMessagesResponse
        }

        val getMateMessagesCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(getMateMessagesCallMock.execute()).thenAnswer {
            getMateMessagesResponseMock
        }

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
        val offset = 0
        val count = 5

        val user = UserDataRepositoryMockContainer.DEFAULT_DATA_USER.copy(username = "test user")
        val resolveUsers = mapOf(
            user.id to user
        )
        val localMessages = listOf(
            MateMessageEntity(0, chatId, user.id, "local one", 1)
        )
        val httpMessages = GetMessagesResponse(listOf(
            GetMessageResponse(0, user.id, "remote one", 2)
        ))

        mLocalSourceGetMateMessages = localMessages
        mHttpSourceGetMateMessagesResponse = httpMessages
        mUserDataRepositoryMockContainer.resolveUsers = resolveUsers

        val expectedLocalDataMessages = localMessages.map { it.toDataMessage(user) }
        val expectedHttpDataMessages = httpMessages.messages.map { it.toDataMessage(user) }

        mDataRepository.resultFlow.test {
            val gottenLocalResult = mDataRepository.getMessages(chatId, offset, count).await()

            Assert.assertTrue(mLocalSourceGetMateMessagesCallFlag)

            val gottenLocalDataMessages = gottenLocalResult.messages

            AssertUtils.assertEqualContent(expectedLocalDataMessages, gottenLocalDataMessages)

            Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)

            val gottenHttpResult = awaitItem()

            Assert.assertTrue(mHttpSourceGetMateMessagesCallFlag)
            Assert.assertTrue(mHttpSourceGetMateMessagesResponseCallFlag)
            Assert.assertEquals(GetMessagesDataResult::class, gottenHttpResult::class)

            val gottenHttpDataMessages = (gottenHttpResult as GetMessagesDataResult).messages

            AssertUtils.assertEqualContent(expectedHttpDataMessages, gottenHttpDataMessages)
        }
    }
}