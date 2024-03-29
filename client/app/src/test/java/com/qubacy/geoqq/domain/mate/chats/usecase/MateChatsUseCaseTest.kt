package com.qubacy.geoqq.domain.mate.chats.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsDataResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.mate.chats.model.toMateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.GetChatChunkDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MateChatsUseCaseTest : UseCaseTest<MateChatsUseCase>() {
    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private var mGetChatsDataResult: GetChatsDataResult? = null

    private var mGetChatsCallFlag = false

    override fun initRepositories(): List<DataRepository> {
        val superRepositories = super.initRepositories()
        val mateChatDataRepositoryMock = Mockito.mock(MateChatDataRepository::class.java)

        runTest {
            Mockito.`when`(mateChatDataRepositoryMock.getChats(
                Mockito.anyInt(), Mockito.anyInt()
            )).thenAnswer {
                mGetChatsCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)

                MutableLiveData(mGetChatsDataResult!!)
            }
        }

        return superRepositories.plus(mateChatDataRepositoryMock)
    }

    override fun initUseCase(repositories: List<DataRepository>) {
        mUseCase = MateChatsUseCase(
            repositories[0] as ErrorDataRepository,
            repositories[1] as MateChatDataRepository
        )
    }

    override fun clear() {
        super.clear()

        mGetChatsDataResult = null

        mGetChatsCallFlag = false
    }

    @Test
    fun getChatChunkFailedTest() = runTest {
        val chunkIndex = 0
        val expectedError = TestError.normal

        mErrorDataRepositoryMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.getChatChunk(chunkIndex)

            val gottenResult = awaitItem()

            Assert.assertFalse(gottenResult.isSuccessful())
            Assert.assertEquals(expectedError, gottenResult.error)
        }
    }

    @Test
    fun getChatChunkSucceededTest() = runTest {
        val chunkIndex = 1
        val count = MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
        val offset = count * chunkIndex
        val mockedUri = UriMockUtil.getMockedUri()

        val dataChats = listOf(
            DataMateChat(0,
                DataUser(0, "test", String(),
                    DataImage(0, mockedUri), false, false),
                0,
                null
            )
        )
        val chats = dataChats.map { it.toMateChat() }

        val getChatsResult = GetChatsDataResult(offset, dataChats)
        val expectedChatChunk = MateChatChunk(chunkIndex, chats)

        mGetChatsDataResult = getChatsResult

        mUseCase.resultFlow.test {
            mUseCase.getChatChunk(chunkIndex)

            val gottenResult = awaitItem()

            Assert.assertEquals(GetChatChunkDomainResult::class, gottenResult::class)

            val gottenChatChunk = (gottenResult as GetChatChunkDomainResult).chunk

            Assert.assertEquals(expectedChatChunk, gottenChatChunk)
        }
    }
}