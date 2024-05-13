package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.mate._common.model.chat.MateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.UpdateChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common._test.context.MateViewModelTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state.MateChatsUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

class MateChatsViewModelImplTest(

) : BusinessViewModelTest<MateChatsUiState, MateChatsUseCase, MateChatsViewModelImpl>(
    MateChatsUseCase::class.java
) {
    companion object {
        val DEFAULT_MATE_CHAT_PRESENTATION = MateViewModelTestContext.DEFAULT_MATE_CHAT_PRESENTATION
    }

    private var mUseCaseGetChatChunkCallFlag = false

    private lateinit var mIsGettingNextChatChunkFieldReflection: Field

    override fun preInit() {
        super.preInit()

        mIsGettingNextChatChunkFieldReflection = MateChatsViewModelImpl::class.java
            .getDeclaredField("mIsGettingNextChatChunk")
            .apply { isAccessible = true }
    }

    override fun clear() {
        super.clear()

        mUseCaseGetChatChunkCallFlag = false
    }

    override fun initUseCase(): MateChatsUseCase {
        val mateChatsUseCaseMock = super.initUseCase()

        Mockito.`when`(mateChatsUseCaseMock.getChatChunk(
            AnyMockUtil.anyObject(), Mockito.anyInt()
        )).thenAnswer {
            mUseCaseGetChatChunkCallFlag = true

            Unit
        }

        return mateChatsUseCaseMock
    }

    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataSource: LocalErrorDatabaseDataSource
    ): MateChatsViewModelImpl {
        return MateChatsViewModelImpl(savedStateHandle, errorDataSource, mUseCase)
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
            val chatChunkSizes: MutableMap<Int, Int>,
            val isGettingNextChatChunk: Boolean,
            val expectedIsNextChatChunkGettingAllowed: Boolean
        )

        val testCases = listOf(
            TestCase(
                mutableMapOf(),
                false,
                true
            ),
            TestCase(
                mutableMapOf(),
                true,
                false
            ),
            TestCase(
                mutableMapOf(0 to 1),
                false,
                false
            ),
            TestCase(
                mutableMapOf(0 to MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE),
                false,
                true
            )
        )

        for (testCase in testCases) {
            println("isNextChatChunkGettingAllowedTest(): " +
                    "chatChunkSizes = ${testCase.chatChunkSizes}; " +
                    "isGettingNextChatChunk = ${testCase.isGettingNextChatChunk};")

            setIsGettingNextChatChunkValue(testCase.isGettingNextChatChunk)
            setUiState(MateChatsUiState(chatChunkSizes = testCase.chatChunkSizes))

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
        val initChatChunkSizes = mutableMapOf<Int, Int>()

        val expectedError = TestError.normal
        val expectedLoadingState = false
        val expectedChatChunkSizes = initChatChunkSizes
        val expectedIsGettingNextChatChunk = false

        val getChatChunkDomainResult = GetChatChunkDomainResult(error = expectedError)

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                error = null,
                chatChunkSizes = initChatChunkSizes
            )
        )
        setIsGettingNextChatChunkValue(initIsGettingNextChatChunk)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getChatChunkDomainResult)

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
            AssertUtils.assertEqualMaps(expectedChatChunkSizes, finalUiState.chatChunkSizes)
            Assert.assertEquals(expectedError, finalUiState.error)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processGetChatChunkDomainResultTest() = runTest {
        val initIsLoading = true
        val initChats = mutableListOf<MateChatPresentation>()
        val initIsGettingNextChatChunk = true
        val initChatChunkSizes = mutableMapOf<Int, Int>()

        val mockedUri = UriMockUtil.getMockedUri()
        val avatar = Image(0, mockedUri)
        val user = User(0, "test", String(), avatar, false, false)
        val chat = MateChat(0, user, 0, null)
        val chatChunk = MateChatChunk(0, listOf(chat))

        val getChatChunkDomainResult = GetChatChunkDomainResult(chunk = chatChunk)

        val expectedChatChunkPosition = chatChunk.offset
        val expectedChatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }
        val expectedLoadingState = false
        val expectedChatChunkSizes = initChatChunkSizes.toMutableMap()
            .apply { this[0] = chatChunk.chats.size }
        val expectedChatCount = initChats.size + expectedChatPresentationChunk.size
        val expectedIsGettingNextChatChunk = false

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                chats = initChats,
                chatChunkSizes = initChatChunkSizes
            )
        )
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
            val gottenIsGettingNextChatChunk = getIsGettingNextChatChunkValue()
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedChatChunkPosition, gottenChatChunkPosition)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, gottenChatPresentationChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedIsGettingNextChatChunk, gottenIsGettingNextChatChunk)
            AssertUtils.assertEqualMaps(expectedChatChunkSizes, finalUiState.chatChunkSizes)
            Assert.assertEquals(expectedChatCount, finalUiState.chats.size)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, finalUiState.chats)
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
            val gottenIsGettingNextChatChunk = getIsGettingNextChatChunkValue()
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
        val initChats = mutableListOf(DEFAULT_MATE_CHAT_PRESENTATION)
        val initChatSizes = mutableMapOf(0 to initChats.size)

        val mockedUri = UriMockUtil.getMockedUri()
        val avatar = Image(0, mockedUri)
        val user = User(0, "test", String(), avatar, false, false)
        val chat = MateChat(0, user, 0, null)
        val chatChunk = MateChatChunk(0, listOf(chat))

        val updateChatChunkDomainResult = UpdateChatChunkDomainResult(chunk = chatChunk)

        val expectedChatCount = chatChunk.chats.size
        val expectedChatChunkSizes = mutableMapOf(
            0 to chatChunk.chats.size
        )
        val expectedChatChunkPosition = chatChunk.offset
        val expectedChatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }
        val expectedChatChunkSizeDelta = initChats.size - chatChunk.chats.size
        val expectedLoadingState = false

        setUiState(
            MateChatsUiState(
                isLoading = initIsLoading,
                chats = initChats,
                chatChunkSizes = initChatSizes
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
            val gottenChatChunkSizeDelta = updateChatChunkOperation.chatChunkSizeDelta
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedChatChunkPosition, gottenChatChunkPosition)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, gottenChatPresentationChunk)
            Assert.assertEquals(expectedChatChunkSizeDelta, gottenChatChunkSizeDelta)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedChatCount, finalUiState.chats.size)
            AssertUtils.assertEqualMaps(expectedChatChunkSizes, finalUiState.chatChunkSizes)
            AssertUtils.assertEqualContent(expectedChatPresentationChunk, finalUiState.chats)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    private fun getIsGettingNextChatChunkValue(): Boolean {
        return mIsGettingNextChatChunkFieldReflection.getBoolean(mModel)
    }

    private fun setIsGettingNextChatChunkValue(value: Boolean) {
        mIsGettingNextChatChunkFieldReflection.setBoolean(mModel, value)
    }
}