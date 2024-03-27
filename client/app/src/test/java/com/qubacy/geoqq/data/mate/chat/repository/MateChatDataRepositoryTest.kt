package com.qubacy.geoqq.data.mate.chat.repository

import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message.GetMessageResponse
import com.qubacy.geoqq.data.error.repository._test.mock.ErrorDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.chat.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.token.repository._test.mock.TokenDataRepositoryMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response

class MateChatDataRepositoryTest : DataRepositoryTest<MateChatDataRepository>() {
    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer

    private var mLocalSourceGetChats: Map<MateChatEntity, MateMessageEntity?>? = null
    private var mLocalSourceGetChatById: Map<MateChatEntity, MateMessageEntity?>? = null

    private var mLocalSourceGetChatsCallFlag = false
    private var mLocalSourceGetChatByIdCallFlag = false
    private var mLocalSourceInsertChatCallFlag = false
    private var mLocalSourceInsertChatWithLastMessageCallFlag = false
    private var mLocalSourceUpdateChatCallFlag = false
    private var mLocalSourceUpdateChatWithLastMessageCallFlag = false
    private var mLocalSourceDeleteChatCallFlag = false
    private var mLocalSourceSaveChatsCallFlag = false

    private var mHttpSourceGetChatResponse: GetChatResponse? = null
    private var mHttpSourceGetChatsResponse: GetChatsResponse? = null

    private var mHttpSourceGetChatResponseCallFlag = false
    private var mHttpSourceGetChatsResponseCallFlag = false
    private var mHttpSourceGetChatCallFlag = false
    private var mHttpSourceGetChatsCallFlag = false

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
        mLocalSourceSaveChatsCallFlag = false

        mHttpSourceGetChatResponse = null
        mHttpSourceGetChatsResponse = null

        mHttpSourceGetChatResponseCallFlag = false
        mHttpSourceGetChatsResponseCallFlag = false
        mHttpSourceGetChatCallFlag = false
        mHttpSourceGetChatsCallFlag = false
    }

    private fun initMateChatDataRepository() {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()

        val localMateChatDataSourceMock = mockLocalMateChatDataSource()
        val httpMateChatDataSourceMock = mockHttpMateChatDataSource()

        mDataRepository = MateChatDataRepository(
            mErrorDataRepository = mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepositoryMock,
            mLocalMateChatDataSource = localMateChatDataSourceMock,
            mHttpMateChatDataSource = httpMateChatDataSourceMock
        )
    }

    private fun mockLocalMateChatDataSource(): LocalMateChatDataSource {
        val localMateChatDataSourceMock = Mockito.mock(LocalMateChatDataSource::class.java)

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
        Mockito.`when`(localMateChatDataSourceMock.saveChats(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceSaveChatsCallFlag = true

            Unit
        }

        return localMateChatDataSourceMock
    }

    private fun mockHttpMateChatDataSource(): HttpMateChatDataSource {
        val getChatResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(getChatResponseMock.body()).thenAnswer {
            mHttpSourceGetChatResponseCallFlag = true
            mHttpSourceGetChatResponse
        }

        val getChatsResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(getChatsResponseMock.body()).thenAnswer {
            mHttpSourceGetChatsResponseCallFlag = true
            mHttpSourceGetChatsResponse
        }

        val getChatCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(getChatCallMock.execute()).thenAnswer {
            getChatResponseMock
        }

        val getChatsCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(getChatsCallMock.execute()).thenAnswer {
            getChatsResponseMock
        }

        val httpMateChatDataSourceMock = Mockito.mock(HttpMateChatDataSource::class.java)

        Mockito.`when`(httpMateChatDataSourceMock.getChat(
            Mockito.anyLong(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceGetChatCallFlag = true

            getChatCallMock
        }
        Mockito.`when`(httpMateChatDataSourceMock.getChats(
            Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceGetChatsCallFlag = true

            getChatsCallMock
        }

        return httpMateChatDataSourceMock
    }

    @Test
    fun getChatsTest() = runTest {
        val offset = 0
        val count = 5

        val localChats = mapOf(
            MateChatEntity(0, 0, 0, 0)
                to MateMessageEntity(0, 0, 0, "local message", 0)
        )
        val httpChats = GetChatsResponse(listOf(
            GetChatResponse(0, 0, 0,
                GetMessageResponse(1, 0, "http message", 20))
        ))

        mLocalSourceGetChats = localChats
        mHttpSourceGetChatsResponse = httpChats

        val expectedLocalDataChats = localChats.map { it.toDataMateChat() }
        val expectedHttpDataChats = httpChats.chats.map { it.toDataMateChat() }

        mDataRepository.resultFlow.test {
            mDataRepository.getChats(offset, count)

            val gottenLocalResult = awaitItem()

            Assert.assertTrue(mLocalSourceGetChatsCallFlag)
            Assert.assertEquals(GetChatsDataResult::class, gottenLocalResult::class)

            val gottenLocalDataChats = (gottenLocalResult as GetChatsDataResult).chats

            AssertUtils.assertEqualContent(expectedLocalDataChats, gottenLocalDataChats)

            Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)

            val gottenHttpResult = awaitItem()

            Assert.assertTrue(mHttpSourceGetChatsCallFlag)
            Assert.assertTrue(mHttpSourceGetChatsResponseCallFlag)
            Assert.assertEquals(GetChatsDataResult::class, gottenHttpResult::class)

            val gottenHttpDataChats = (gottenHttpResult as GetChatsDataResult).chats

            AssertUtils.assertEqualContent(expectedHttpDataChats, gottenHttpDataChats)
        }
    }
}