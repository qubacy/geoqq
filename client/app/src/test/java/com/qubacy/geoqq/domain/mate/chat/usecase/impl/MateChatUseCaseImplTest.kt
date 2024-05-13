package com.qubacy.geoqq.domain.mate.chat.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common._test.context.MateMessageDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.message.repository._common.result.GetMessagesDataResult
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.interlocutor.usecase._common.InterlocutorUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase._common._test.mock.InterlocutorUseCaseMockContainer
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._common._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate._common.model.message.toMateMessage
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._common._test.mock.MateRequestUseCaseMockContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MateChatUseCaseImplTest : UseCaseTest<MateChatUseCaseImpl>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
        val DEFAULT_DATA_MESSAGE = MateMessageDataRepositoryTestContext.DEFAULT_DATA_MESSAGE
    }

    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mMateRequestUseCaseMockContainer: MateRequestUseCaseMockContainer
    private lateinit var mInterlocutorUseCaseMockContainer: InterlocutorUseCaseMockContainer
    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer

    private var mGetMessagesResults: List<GetMessagesDataResult>? = null

    private var mGetMessagesCallFlag = false
    private var mSendMessageCallFlag = false

    private var mDeleteChatCallFlag = false

    private var mCreateMateRequestCallFlag = false

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mMateRequestUseCaseMockContainer = MateRequestUseCaseMockContainer()
        mInterlocutorUseCaseMockContainer = InterlocutorUseCaseMockContainer()
        mLogoutUseCaseMockContainer = LogoutUseCaseMockContainer()

        val mateMessageDataRepositoryMock = mockMateMessageDataRepository()
        val mateChatDataRepositoryMock = mockMateChatDataRepository()

        return superDependencies.plus(listOf(
            mMateRequestUseCaseMockContainer.mateRequestUseCaseMock,
            mInterlocutorUseCaseMockContainer.interlocutorUseCaseMock,
            mLogoutUseCaseMockContainer.logoutUseCaseMock,
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

                if (mErrorDataSourceMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)

                val resultLiveData = MutableLiveData<GetMessagesDataResult>()

                CoroutineScope(Dispatchers.Unconfined).launch {
                    for (result in mGetMessagesResults!!) resultLiveData.postValue(result)
                }

                resultLiveData
            }
            Mockito.`when`(mateMessageDataRepositoryMock.sendMessage(
                Mockito.anyLong(),
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mSendMessageCallFlag = true

                if (mErrorDataSourceMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)
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

                if (mErrorDataSourceMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)
            }
        }

        return mateChatDataRepositoryMock
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MateChatUseCaseImpl(
            errorSource = dependencies[0] as LocalErrorDatabaseDataSource,
            mMateRequestUseCase = dependencies[1] as MateRequestUseCase,
            mInterlocutorUseCase = dependencies[2] as InterlocutorUseCase,
            mLogoutUseCase = dependencies[3] as LogoutUseCase,
            mMateMessageDataRepository = dependencies[4] as MateMessageDataRepository,
            mMateChatDataRepository = dependencies[5] as MateChatDataRepository
        )
    }

    override fun clear() {
        super.clear()

        mMateRequestUseCaseMockContainer.clear()
        mInterlocutorUseCaseMockContainer.clear()

        mGetMessagesResults = null

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

        val localMessages = listOf(
            DEFAULT_DATA_MESSAGE
        )
        val remoteMessages = listOf(
            DEFAULT_DATA_MESSAGE,
            DEFAULT_DATA_MESSAGE
        )

        val localGetMessagesResult = GetMessagesDataResult(false, offset, localMessages)
        val remoteGetMessagesResult = GetMessagesDataResult(true, offset, remoteMessages)

        val getMessagesResults = listOf(localGetMessagesResult, remoteGetMessagesResult)

        val expectedLocalMessageChunk =
            MateMessageChunk(chunkIndex, localMessages.map { it.toMateMessage() })
        val expectedRemoteMessageChunk =
            MateMessageChunk(chunkIndex, remoteMessages.map { it.toMateMessage() })

        mGetMessagesResults = getMessagesResults

        mUseCase.resultFlow.test {
            mUseCase.getMessageChunk(chatId, loadedMessageIds, chunkIndex)

            val getResult = awaitItem()

            Assert.assertTrue(mGetMessagesCallFlag)
            Assert.assertEquals(GetMessageChunkDomainResult::class, getResult::class)
            Assert.assertTrue(getResult.isSuccessful())

            val gottenLocalMessageChunk = (getResult as GetMessageChunkDomainResult).chunk!!

            Assert.assertEquals(expectedLocalMessageChunk, gottenLocalMessageChunk)

            val updateResult = awaitItem()

            Assert.assertEquals(UpdateMessageChunkDomainResult::class, updateResult::class)
            Assert.assertTrue(updateResult.isSuccessful())

            val gottenRemoteMessageChunk = (updateResult as UpdateMessageChunkDomainResult).chunk!!

            Assert.assertEquals(expectedRemoteMessageChunk, gottenRemoteMessageChunk)
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