package com.qubacy.geoqq.domain.mate.chat.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesDataResult
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase._test.mock.InterlocutorUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.chat.model.toMateMessage
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._test.mock.MateRequestUseCaseMockContainer
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

    private lateinit var mMateRequestUseCaseMockContainer: MateRequestUseCaseMockContainer
    private lateinit var mInterlocutorUseCaseMockContainer: InterlocutorUseCaseMockContainer

    private var mGetMessagesResult: GetMessagesDataResult? = null

    private var mGetMessagesCallFlag = false
    private var mSendMessageCallFlag = false

    private var mDeleteChatCallFlag = false

    private var mCreateMateRequestCallFlag = false

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mMateRequestUseCaseMockContainer = MateRequestUseCaseMockContainer()
        mInterlocutorUseCaseMockContainer = InterlocutorUseCaseMockContainer()

        val mateMessageDataRepositoryMock = mockMateMessageDataRepository()
        val mateChatDataRepositoryMock = mockMateChatDataRepository()

        return superDependencies.plus(listOf(
            mMateRequestUseCaseMockContainer.mateRequestUseCaseMock,
            mInterlocutorUseCaseMockContainer.interlocutorUseCaseMock,
            mateMessageDataRepositoryMock,
            mateChatDataRepositoryMock
        ))
    }

    private fun mockMateMessageDataRepository(): MateMessageDataRepository {
        val mateMessageDataRepositoryMock = Mockito.mock(MateMessageDataRepository::class.java)

        runTest {
            Mockito.`when`(mateMessageDataRepositoryMock.getMessages(
                Mockito.anyLong(),
                AnyMockUtil.anyObject(),
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

        return mateMessageDataRepositoryMock
    }

    private fun mockMateChatDataRepository(): MateChatDataRepository {
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

        return mateChatDataRepositoryMock
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MateChatUseCase(
            errorDataRepository = dependencies[0] as ErrorDataRepository,
            mMateRequestUseCase = dependencies[1] as MateRequestUseCase,
            mInterlocutorUseCase = dependencies[2] as InterlocutorUseCase,
            mMateMessageDataRepository = dependencies[3] as MateMessageDataRepository,
            mMateChatDataRepository = dependencies[4] as MateChatDataRepository
        )
    }

    override fun clear() {
        super.clear()

        mMateRequestUseCaseMockContainer.clear()
        mInterlocutorUseCaseMockContainer.clear()

        mGetMessagesResult = null

        mGetMessagesCallFlag = false
        mSendMessageCallFlag = false

        mDeleteChatCallFlag = false

        mCreateMateRequestCallFlag = false
    }

    @Test
    fun getMessageChunkTest() = runTest {
        val chatId = 0L
        val loadedMessageIds = listOf<Long>()
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
            mUseCase.getMessageChunk(chatId, loadedMessageIds, chunkIndex)

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
        val interlocutorId = 0L

        mUseCase.getInterlocutor(interlocutorId)

        Assert.assertTrue(mInterlocutorUseCaseMockContainer.getInterlocutorCallFlag)
    }

    @Test
    fun sendMateRequestTest() = runTest {
        val interlocutorId = 0L

        mUseCase.sendMateRequestToInterlocutor(interlocutorId)

        Assert.assertTrue(mMateRequestUseCaseMockContainer.sendMateRequestCallFlag)
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