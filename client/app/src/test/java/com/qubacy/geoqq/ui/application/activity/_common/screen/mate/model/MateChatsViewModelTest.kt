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
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

class MateChatsViewModelTest(

) : BusinessViewModelTest<MateChatsUiState, MateChatsUseCase, MateChatsViewModel>(
    MateChatsUseCase::class.java
) {
    companion object {
        val DEFAULT_IMAGE_PRESENTATION = ImagePresentation(0, UriMockUtil.getMockedUri())
        val DEFAULT_USER_PRESENTATION = UserPresentation(
            0, "test user", String(),
            DEFAULT_IMAGE_PRESENTATION, false, false
        )
        val DEFAULT_LAST_MESSAGE_PRESENTATION = MateMessagePresentation(
            0, DEFAULT_USER_PRESENTATION, "test message", String()
        )
        val DEFAULT_MATE_CHAT_PRESENTATION = MateChatPresentation(
            0, DEFAULT_USER_PRESENTATION, 0, DEFAULT_LAST_MESSAGE_PRESENTATION)
    }

    private var mUseCaseGetChatChunkCallFlag = false

    private lateinit var mLastChatChunkIndexFieldReflection: Field
    private lateinit var mIsGettingNextChatChunkFieldReflection: Field

    override fun preInit() {
        super.preInit()

        mLastChatChunkIndexFieldReflection = MateChatsViewModel::class.java
            .getDeclaredField("mLastChatChunkIndex")
            .apply { isAccessible = true }
        mIsGettingNextChatChunkFieldReflection = MateChatsViewModel::class.java
            .getDeclaredField("mIsGettingNextChatChunk")
            .apply { isAccessible = true }
    }

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
        val expectedIsGettingNewChatChunkValue = true

        mModel.uiOperationFlow.test {
            mModel.getNextChatChunk()

            val operation = awaitItem()

            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)
            Assert.assertTrue(mUseCaseGetChatChunkCallFlag)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading
            val gottenIsGettingNewChatChunkValue = getIsGettingNextChatChunkValue()

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedIsGettingNewChatChunkValue, gottenIsGettingNewChatChunkValue)
        }
    }

    @Test
    fun isNextChatChunkGettingAllowedTest() {
        data class TestCase(
            val lastChatChunkIndex: Int,
            val chatChunks: MutableMap<Int, List<MateChatPresentation>>,
            val isGettingNextChatChunk: Boolean,
            val expectedIsNextChatChunkGettingAllowed: Boolean
        )

        val testCases = listOf(
            TestCase(
                0,
                mutableMapOf(),
                false,
                true
            ),
            TestCase(
                0,
                mutableMapOf(),
                true,
                false
            ),
            TestCase(
                0,
                mutableMapOf(0 to listOf(DEFAULT_MATE_CHAT_PRESENTATION)),
                false,
                true
            ),
            TestCase(
                0,
                mutableMapOf(0 to listOf(DEFAULT_MATE_CHAT_PRESENTATION)),
                true,
                false
            ),
            TestCase(
                1,
                mutableMapOf(0 to listOf(DEFAULT_MATE_CHAT_PRESENTATION)),
                false,
                false
            ),
            TestCase(
                1,
                mutableMapOf(0 to mutableListOf<MateChatPresentation>()
                    .apply {
                        repeat(MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE) {
                            add(DEFAULT_MATE_CHAT_PRESENTATION)
                        }
                    }),
                false,
                true
            )
        )

        for (testCase in testCases) {
            setLastChatChunkIndexValue(testCase.lastChatChunkIndex)
            setIsGettingNextChatChunkValue(testCase.isGettingNextChatChunk)
            setUiState(MateChatsUiState(chatChunks = testCase.chatChunks))

            val gottenIsNextChatChunkGettingAllowed = mModel.isNextChatChunkGettingAllowed()

            Assert.assertEquals(
                testCase.expectedIsNextChatChunkGettingAllowed,
                gottenIsNextChatChunkGettingAllowed
            )
        }
    }

    @Test
    fun processGetChatChunkDomainResultWithErrorTest() = runTest {
        val initIsLoading = true
        val initIsGettingNextChatChunk = true
        val initLastChatChunkIndex = 0

        val expectedError = TestError.normal
        val expectedLoadingState = false
        val expectedLastChatChunkIndex = initLastChatChunkIndex
        val expectedIsGettingNextChatChunk = false

        val getChatChunkDomainResult = GetChatChunkDomainResult(error = expectedError)

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                error = null
            )
        )
        setLastChatChunkIndexValue(initLastChatChunkIndex)
        setIsGettingNextChatChunkValue(initIsGettingNextChatChunk)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getChatChunkDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenLastChatChunkIndexValue = getLastChatChunkIndexValue()
            val gottenIsGettingNextChatChunk = getIsGettingNextChatChunkValue()
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLastChatChunkIndex, gottenLastChatChunkIndexValue)
            Assert.assertEquals(expectedIsGettingNextChatChunk, gottenIsGettingNextChatChunk)
            Assert.assertEquals(expectedError, finalUiState.error)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processGetChatChunkDomainResultTest() = runTest {
        val initIsLoading = true
        val initChatChunks = mutableMapOf<Int, List<MateChatPresentation>>()
        val initIsGettingNextChatChunk = true
        val initLastChatChunkIndex = 0

        val mockedUri = UriMockUtil.getMockedUri()
        val avatar = Image(0, mockedUri)
        val user = User(0, "test", String(), avatar, false, false)
        val chat = MateChat(0, user, 0, null)
        val chatChunk = MateChatChunk(0, listOf(chat))

        val getChatChunkDomainResult = GetChatChunkDomainResult(chunk = chatChunk)

        val expectedChatChunkPosition = chatChunk.index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
        val expectedChatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }
        val expectedLoadingState = false
        val expectedLastChatChunkIndex = initLastChatChunkIndex + 1
        val expectedIsGettingNextChatChunk = false

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                chatChunks = initChatChunks
            )
        )
        setLastChatChunkIndexValue(initLastChatChunkIndex)
        setIsGettingNextChatChunkValue(initIsGettingNextChatChunk)

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
            val gottenLastChatChunkIndexValue = getLastChatChunkIndexValue()
            val gottenIsGettingNextChatChunk = getIsGettingNextChatChunkValue()
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedChatChunkPosition, gottenChatChunkPosition)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, gottenChatPresentationChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLastChatChunkIndex, gottenLastChatChunkIndexValue)
            Assert.assertEquals(expectedIsGettingNextChatChunk, gottenIsGettingNextChatChunk)
            Assert.assertTrue(finalUiState.chatChunks.contains(chatChunk.index))
            AssertUtils.assertEqualContent(
                expectedChatPresentationChunk, finalUiState.chatChunks[chatChunk.index]!!)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processUpdateChatChunkDomainResultWithErrorTest() = runTest {
        val initIsLoading = true
        val initIsGettingNextChatChunk = true

        val expectedError = TestError.normal
        val expectedLoadingState = false
        val expectedIsGettingNextChatChunk = false

        val updateChatChunkDomainResult = UpdateChatChunkDomainResult(error = expectedError)

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                error = null
            )
        )
        setIsGettingNextChatChunkValue(initIsGettingNextChatChunk)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateChatChunkDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenIsGettingNextChatChunk = getIsGettingNextChatChunkValue()
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedIsGettingNextChatChunk, gottenIsGettingNextChatChunk)
            Assert.assertEquals(expectedError, finalUiState.error)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processUpdateChatChunkDomainResultTest() = runTest {
        val initIsLoading = true
        val initIsGettingNextChatChunk = true
        val initLastChatChunkIndex = 0
        val initChatChunks = mutableMapOf(
            initLastChatChunkIndex to listOf(DEFAULT_MATE_CHAT_PRESENTATION)
        )

        val mockedUri = UriMockUtil.getMockedUri()
        val avatar = Image(0, mockedUri)
        val user = User(0, "test", String(), avatar, false, false)
        val chat = MateChat(0, user, 0, null)
        val chatChunk = MateChatChunk(0, listOf(chat))

        val updateChatChunkDomainResult = UpdateChatChunkDomainResult(chunk = chatChunk)

        val expectedChatChunkPosition = chatChunk.index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
        val expectedChatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }
        val expectedChatChunkSizeDelta = initChatChunks[initLastChatChunkIndex]!!.size -
                chatChunk.chats.size
        val expectedLoadingState = false
        val expectedIsGettingNextChatChunk = false

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                chatChunks = initChatChunks
            )
        )
        setIsGettingNextChatChunkValue(initIsGettingNextChatChunk)
        setLastChatChunkIndexValue(initLastChatChunkIndex)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateChatChunkDomainResult)

            val updateChatChunkOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(UpdateChatChunkUiOperation::class, updateChatChunkOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            updateChatChunkOperation as UpdateChatChunkUiOperation

            val gottenChatChunkPosition = updateChatChunkOperation.position
            val gottenChatPresentationChunk = updateChatChunkOperation.chats
            val gottenChatChunkSizeDelta = updateChatChunkOperation.chatChunkSizeDelta
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenIsGettingNextChatChunk = getIsGettingNextChatChunkValue()
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedChatChunkPosition, gottenChatChunkPosition)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, gottenChatPresentationChunk)
            Assert.assertEquals(expectedChatChunkSizeDelta, gottenChatChunkSizeDelta)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedIsGettingNextChatChunk, gottenIsGettingNextChatChunk)
            Assert.assertTrue(finalUiState.chatChunks.contains(chatChunk.index))
            AssertUtils.assertEqualContent(
                expectedChatPresentationChunk, finalUiState.chatChunks[chatChunk.index]!!)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    private fun getLastChatChunkIndexValue(): Int {
        return mLastChatChunkIndexFieldReflection.getInt(mModel)
    }

    private fun setLastChatChunkIndexValue(value: Int) {
        mLastChatChunkIndexFieldReflection.set(mModel, value)
    }

    private fun getIsGettingNextChatChunkValue(): Boolean {
        return mIsGettingNextChatChunkFieldReflection.getBoolean(mModel)
    }

    private fun setIsGettingNextChatChunkValue(value: Boolean) {
        mIsGettingNextChatChunkFieldReflection.setBoolean(mModel, value)
    }
}