package com.qubacy.geoqq.ui.application.activity._common.screen.mate.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.UpdateChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class MateChatsViewModelTest(

) : BusinessViewModelTest<MateChatsUiState, MateChatsUseCase, MateChatsViewModel>(
    MateChatsUseCase::class.java
) {
    private var mUseCaseGetChatChunkCallFlag = false

    override fun clear() {
        super.clear()

        mUseCaseGetChatChunkCallFlag = false
    }

    override fun initUseCase(): MateChatsUseCase {
        val mateChatsUseCaseMock = super.initUseCase()

        Mockito.`when`(mateChatsUseCaseMock.getChatChunk(Mockito.anyInt())).thenAnswer {
            mUseCaseGetChatChunkCallFlag = true

            Unit
        }

        return mateChatsUseCaseMock
    }

    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataRepository: ErrorDataRepository
    ): MateChatsViewModel {
        return MateChatsViewModel(savedStateHandle, errorDataRepository, mUseCase)
    }

    @Test
    fun getNextChatChunkTest() = runTest {
        val expectedLoadingState = true

        mModel.uiOperationFlow.test {
            mModel.getNextChatChunk()

            val operation = awaitItem()

            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)
            Assert.assertTrue(mUseCaseGetChatChunkCallFlag)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
        }
    }

    @Test
    fun processGetChatChunkDomainResultWithErrorTest() = runTest {
        val initIsLoading = true
        val expectedError = TestError.normal
        val expectedLoadingState = false
        val getChatChunkDomainResult = GetChatChunkDomainResult(error = expectedError)

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                error = null
            )
        )

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getChatChunkDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedError, finalUiState.error)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processGetChatChunkDomainResultTest() = runTest {
        val initIsLoading = true
        val initChatChunks = mutableMapOf<Int, List<MateChatPresentation>>()

        val mockedUri = UriMockUtil.getMockedUri()
        val avatar = Image(0, mockedUri)
        val user = User(0, "test", String(), avatar, false, false)
        val chat = MateChat(0, user, 0, null)
        val chatChunk = MateChatChunk(0, listOf(chat))

        val getChatChunkDomainResult = GetChatChunkDomainResult(chunk = chatChunk)

        val expectedChatChunkPosition = chatChunk.index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
        val expectedChatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }
        val expectedLoadingState = false

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                chatChunks = initChatChunks
            )
        )

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getChatChunkDomainResult)

            val insertChatsOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(InsertChatsUiOperation::class, insertChatsOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            insertChatsOperation as InsertChatsUiOperation

            val gottenChatChunkPosition = insertChatsOperation.position
            val gottenChatPresentationChunk = insertChatsOperation.chats
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedChatChunkPosition, gottenChatChunkPosition)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, gottenChatPresentationChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)

            Assert.assertTrue(finalUiState.chatChunks.contains(chatChunk.index))
            AssertUtils.assertEqualContent(
                expectedChatPresentationChunk, finalUiState.chatChunks[chatChunk.index]!!)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processUpdateChatChunkDomainResultWithErrorTest() = runTest {
        val initIsLoading = true
        val expectedError = TestError.normal
        val expectedLoadingState = false
        val updateChatChunkDomainResult = UpdateChatChunkDomainResult(error = expectedError)

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                error = null
            )
        )

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateChatChunkDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedError, finalUiState.error)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processUpdateChatChunkDomainResultTest() = runTest {
        val initIsLoading = true
        val initChatChunks = mutableMapOf<Int, List<MateChatPresentation>>()

        val mockedUri = UriMockUtil.getMockedUri()
        val avatar = Image(0, mockedUri)
        val user = User(0, "test", String(), avatar, false, false)
        val chat = MateChat(0, user, 0, null)
        val chatChunk = MateChatChunk(0, listOf(chat))

        val updateChatChunkDomainResult = UpdateChatChunkDomainResult(chunk = chatChunk)

        val expectedChatChunkPosition = chatChunk.index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
        val expectedChatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }
        val expectedLoadingState = false

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                chatChunks = initChatChunks
            )
        )

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateChatChunkDomainResult)

            val updateChatChunkOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(UpdateChatChunkUiOperation::class, updateChatChunkOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            updateChatChunkOperation as UpdateChatChunkUiOperation

            val gottenChatChunkPosition = updateChatChunkOperation.position
            val gottenChatPresentationChunk = updateChatChunkOperation.chats
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedChatChunkPosition, gottenChatChunkPosition)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, gottenChatPresentationChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)

            Assert.assertTrue(finalUiState.chatChunks.contains(chatChunk.index))
            AssertUtils.assertEqualContent(
                expectedChatPresentationChunk, finalUiState.chatChunks[chatChunk.index]!!)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }
}