package com.qubacy.geoqq.domain.mate.chat.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesDataResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.mate.chat.model.toMateMessage
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.SendMateRequestDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MateChatUseCaseTest : UseCaseTest<MateChatUseCase>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryMockContainer.DEFAULT_DATA_USER
        val DEFAULT_DATA_MESSAGE = DataMessage(0, DEFAULT_DATA_USER, "test", 0L)
    }

    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mGetMessagesResult: GetMessagesDataResult? = null

    private var mGetMessagesCallFlag = false
    private var mSendMessageCallFlag = false

    private var mDeleteChatCallFlag = false

    private var mCreateMateRequestCallFlag = false

    override fun initRepositories(): List<DataRepository> {
        val superRepositories = super.initRepositories()

        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val mateMessageDataRepositoryMock = Mockito.mock(MateMessageDataRepository::class.java)

        runTest {
            Mockito.`when`(mateMessageDataRepositoryMock.getMessages(
                Mockito.anyLong(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )).thenAnswer {
                mGetMessagesCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)

                MutableLiveData<GetMessagesDataResult>(mGetMessagesResult)
            }
            Mockito.`when`(mateMessageDataRepositoryMock.sendMessage(
                Mockito.anyLong(),
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mSendMessageCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)
            }
        }

        val mateChatDataRepositoryMock = Mockito.mock(MateChatDataRepository::class.java)

        runTest {
            Mockito.`when`(mateChatDataRepositoryMock.deleteChat(
                Mockito.anyLong()
            )).thenAnswer {
                mDeleteChatCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)
            }
        }

        val mateRequestDataRepositoryMock = Mockito.mock(MateRequestDataRepository::class.java)

        runTest {
            Mockito.`when`(mateRequestDataRepositoryMock.createMateRequest(
                Mockito.anyLong()
            )).thenAnswer {
                mCreateMateRequestCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)
            }
        }

        return superRepositories.plus(listOf(
            mateMessageDataRepositoryMock,
            mateChatDataRepositoryMock,
            mateRequestDataRepositoryMock,
            mUserDataRepositoryMockContainer.userDataRepository
        ))
    }

    override fun initUseCase(repositories: List<DataRepository>) {
        mUseCase = MateChatUseCase(
            errorDataRepository = repositories[0] as ErrorDataRepository,
            mMateMessageDataRepository = repositories[1] as MateMessageDataRepository,
            mMateChatDataRepository = repositories[2] as MateChatDataRepository,
            mMateRequestDataRepository = repositories[3] as MateRequestDataRepository,
            mUserDataRepository = repositories[4] as UserDataRepository
        )
    }

    override fun clear() {
        super.clear()

        mGetMessagesResult = null

        mGetMessagesCallFlag = false
        mSendMessageCallFlag = false

        mDeleteChatCallFlag = false

        mCreateMateRequestCallFlag = false
    }

    @Test
    fun getMessageChunkTest() = runTest {
        val chatId = 0L
        val chunkIndex = 0
        val offset = chunkIndex * MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE
        val messages = listOf(
            DEFAULT_DATA_MESSAGE,
            DEFAULT_DATA_MESSAGE
        )
        val getMessagesResult = GetMessagesDataResult(offset, messages)

        val expectedMessageChunk = MateMessageChunk(chunkIndex, messages.map { it.toMateMessage() })

        mGetMessagesResult = getMessagesResult

        mUseCase.resultFlow.test {
            mUseCase.getMessageChunk(chatId, chunkIndex)

            val result = awaitItem()

            Assert.assertTrue(mGetMessagesCallFlag)
            Assert.assertEquals(GetMessageChunkDomainResult::class, result::class)
            Assert.assertTrue(result.isSuccessful())

            val gottenMessageChunk = (result as GetMessageChunkDomainResult).chunk!!

            Assert.assertEquals(expectedMessageChunk, gottenMessageChunk)
        }
    }

    @Test
    fun getInterlocutorTest() = runTest {
        val user = DEFAULT_DATA_USER
        val getUsersByIdsResult = GetUsersByIdsDataResult(listOf(user))

        val expectedUser = user.toUser()

        mUserDataRepositoryMockContainer.getUsersByIds = getUsersByIdsResult

        mUseCase.resultFlow.test {
            mUseCase.getInterlocutor(user.id)

            val result = awaitItem()

            Assert.assertTrue(mUserDataRepositoryMockContainer.getUsersByIdsCallFlag)
            Assert.assertEquals(GetInterlocutorDomainResult::class, result::class)
            Assert.assertTrue(result.isSuccessful())

            val gottenUser = (result as GetInterlocutorDomainResult).interlocutor

            Assert.assertEquals(expectedUser, gottenUser)
        }
    }

    @Test
    fun sendMateRequestToInterlocutorTest() = runTest {
        val interlocutor = DEFAULT_DATA_USER

        mUseCase.resultFlow.test {
            mUseCase.sendMateRequestToInterlocutor(interlocutor.id)

            val result = awaitItem()

            Assert.assertTrue(mCreateMateRequestCallFlag)
            Assert.assertEquals(SendMateRequestDomainResult::class, result::class)
            Assert.assertTrue(result.isSuccessful())
        }
    }

    @Test
    fun deleteChatTest() = runTest {
        val chatId = 0L

        mUseCase.resultFlow.test {
            mUseCase.deleteChat(chatId)

            val result = awaitItem()

            Assert.assertTrue(mDeleteChatCallFlag)
            Assert.assertEquals(DeleteChatDomainResult::class, result::class)
            Assert.assertTrue(result.isSuccessful())
        }
    }
}