package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.domain.mate._common._test.context.MateUseCaseTestContext
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.delete.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common._test.context.ScreenTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common._test.context.MateTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.request.ChatDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.context.ChatContextUpdatedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.state.MateChatUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

class MateChatViewModelImplTest(

) : BusinessViewModelTest<MateChatUiState, MateChatUseCase, MateChatViewModelImpl>(
    MateChatUseCase::class.java
) {
    companion object {
        val DEFAULT_USER = UseCaseTestContext.DEFAULT_USER
        val DEFAULT_MATE_MESSAGE = MateUseCaseTestContext.DEFAULT_MATE_MESSAGE

        val DEFAULT_USER_PRESENTATION = ScreenTestContext.DEFAULT_USER_PRESENTATION
        val DEFAULT_MATE_CHAT_PRESENTATION = MateTestContext.DEFAULT_MATE_CHAT_PRESENTATION
        val DEFAULT_MATE_MESSAGE_PRESENTATION = MateTestContext
            .DEFAULT_MATE_MESSAGE_PRESENTATION
    }

    private lateinit var mIsGettingNextMessageChunkFieldReflection: Field

    private var mGetMessageChunkCallFlag = false
    private var mGetInterlocutorCallFlag = false
    private var mSendMateRequestToInterlocutorCallFlag = false
    private var mDeleteChatCallFlag = false

    override fun preInit() {
        super.preInit()

        mIsGettingNextMessageChunkFieldReflection = MateChatViewModelImpl::class.java
            .getDeclaredField("mIsGettingNextMessageChunk")
            .apply { isAccessible = true }
    }

    override fun clear() {
        super.clear()

        mGetMessageChunkCallFlag = false
        mGetInterlocutorCallFlag = false
        mSendMateRequestToInterlocutorCallFlag = false
        mDeleteChatCallFlag = false
    }

    override fun initUseCase(): MateChatUseCase {
        val mateChatUseCaseMock = super.initUseCase()

        Mockito.`when`(mateChatUseCaseMock.getMessageChunk(
            Mockito.anyLong(), AnyMockUtil.anyObject(), Mockito.anyInt()
        )).thenAnswer {
            mGetMessageChunkCallFlag = true

            Unit
        }
        Mockito.`when`(mateChatUseCaseMock.getInterlocutor(Mockito.anyLong())).thenAnswer {
            mGetInterlocutorCallFlag = true

            Unit
        }
        Mockito.`when`(mateChatUseCaseMock.sendMateRequestToInterlocutor(
            Mockito.anyLong()
        )).thenAnswer {
            mSendMateRequestToInterlocutorCallFlag = true

            Unit
        }
        Mockito.`when`(mateChatUseCaseMock.deleteChat(Mockito.anyLong())).thenAnswer {
            mDeleteChatCallFlag = true

            Unit
        }

        return mateChatUseCaseMock
    }

    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataSource: LocalErrorDatabaseDataSource
    ): MateChatViewModelImpl {
        return MateChatViewModelImpl(savedStateHandle, errorDataSource, mUseCase)
    }

    @Test
    fun setChatContextTest() {
        val initChatContext = null
        val initUiState = MateChatUiState(chatContext = initChatContext)

        val expectedChatContext = DEFAULT_MATE_CHAT_PRESENTATION

        setUiState(initUiState)

        mModel.setChatContext(expectedChatContext)

        val gottenChatContext = mModel.uiState.chatContext

        Assert.assertEquals(expectedChatContext, gottenChatContext)
    }

    @Test
    fun getNextMessageChunkTest() = runTest {
        val initLoadingState = false
        val initIsGettingNextMessageChunk = false
        val initChatContext = DEFAULT_MATE_CHAT_PRESENTATION
        val initUiState = MateChatUiState(
            isLoading = initLoadingState,
            chatContext = initChatContext
        )

        val expectedIsGettingNextMessageChunk = true
        val expectedLoadingState = true

        setUiState(initUiState)
        mIsGettingNextMessageChunkFieldReflection.set(mModel, initIsGettingNextMessageChunk)

        mModel.uiOperationFlow.test {
            mModel.getNextMessageChunk()

            val operation = awaitItem()

            Assert.assertTrue(mGetMessageChunkCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenIsGettingNextMessageChunk = mIsGettingNextMessageChunkFieldReflection
                .get(mModel)
            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading

            Assert.assertEquals(expectedIsGettingNextMessageChunk, gottenIsGettingNextMessageChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
        }
    }

    @Test
    fun isInterlocutorChatableTest() {
        class TestCase(
            val isUserDeleted: Boolean,
            val isUserMate: Boolean,
            val expectedIsChatable: Boolean
        )

        val testCases = listOf(
            TestCase(false, false, false),
            TestCase(false, true, true),
            TestCase(true, false, false),
            TestCase(true, true, false)
        )

        for (testCase in testCases) {
            val userPresentation = DEFAULT_USER_PRESENTATION.copy(
                isMate = testCase.isUserMate, isDeleted = testCase.isUserDeleted)

            val gottenIsChatable = mModel.isInterlocutorChatable(userPresentation)

            Assert.assertEquals(testCase.expectedIsChatable, gottenIsChatable)
        }
    }

    @Test
    fun isInterlocutorMateableTest() {
        class TestCase(
            val isUserMate: Boolean,
            val isMateRequestSendingAllowed: Boolean,
            val expectedIsMateable: Boolean
        )

        val testCases = listOf(
            TestCase(false, false, false),
            TestCase(false, true, true),
            TestCase(true, false, false),
            TestCase(true, true, false)
        )

        for (testCase in testCases) {
            val userPresentation = DEFAULT_USER_PRESENTATION.copy(isMate = testCase.isUserMate)

            setUiState(
                MateChatUiState(
                isMateRequestSendingAllowed = testCase.isMateRequestSendingAllowed
            )
            )

            val gottenIsMateable = mModel.isInterlocutorMateable(userPresentation)

            Assert.assertEquals(testCase.expectedIsMateable, gottenIsMateable)
        }
    }

    @Test
    fun isInterlocutorMateableOrDeletableTest() {
        class TestCase(
            val isUserMate: Boolean,
            val isMateRequestSendingAllowed: Boolean,
            val expectedIsMateableOrDeletable: Boolean
        )

        val testCases = listOf(
            TestCase(false, false, false),
            TestCase(false, true, true),
            TestCase(true, false, true),
            TestCase(true, true, true)
        )

        for (testCase in testCases) {
            val userPresentation = DEFAULT_USER_PRESENTATION.copy(isMate = testCase.isUserMate)

            setUiState(
                MateChatUiState(
                isMateRequestSendingAllowed = testCase.isMateRequestSendingAllowed
            )
            )

            val gottenIsMateableOrDeletable = mModel.isInterlocutorMateableOrDeletable(
                userPresentation)

            Assert.assertEquals(testCase.expectedIsMateableOrDeletable, gottenIsMateableOrDeletable)
        }
    }

    @Test
    fun isChatDeletableTest() {
        class TestCase(
            val isUserDeleted: Boolean,
            val isUserMate: Boolean,
            val expectedIsChatDeletable: Boolean
        )

        val testCases = listOf(
            TestCase(false, false, true),
            TestCase(false, true, false),
            TestCase(true, false, true),
            TestCase(true, true, true),
        )

        for (testCase in testCases) {
            val userPresentation = DEFAULT_USER_PRESENTATION.copy(
                isDeleted = testCase.isUserDeleted, isMate = testCase.isUserMate)

            val gottenIsChatDeletable = mModel.isChatDeletable(userPresentation)

            Assert.assertEquals(testCase.expectedIsChatDeletable, gottenIsChatDeletable)
        }
    }

    @Test
    fun isInterlocutorMateTest() {
        class TestCase(
            val isUserMate: Boolean,
            val expectedIsUserMate: Boolean
        )

        val testCases = listOf(
            TestCase(false, false),
            TestCase(true, true)
        )

        for (testCase in testCases) {
            val userPresentation = DEFAULT_USER_PRESENTATION.copy(isMate = testCase.isUserMate)
            val chatContext = DEFAULT_MATE_CHAT_PRESENTATION.copy(user = userPresentation)

            setUiState(MateChatUiState(chatContext = chatContext))

            val gottenIsUserMate = mModel.isInterlocutorMate()

            Assert.assertEquals(testCase.expectedIsUserMate, gottenIsUserMate)
        }
    }

    @Test
    fun isNextMessageChunkGettingAllowedTest() {
        class TestCase(
            val messageChunkSizes: MutableMap<Int, Int>,
            val isGettingNextMessageChunk: Boolean,
            val expectedIsNextMessageChunkGettingAllowed: Boolean
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
                mutableMapOf(0 to 0),
                false,
                false
            ),
            TestCase(
                mutableMapOf(0 to MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE - 1),
                false,
                false
            ),
            TestCase(
                mutableMapOf(0 to MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE),
                false,
                true
            )
        )

        for (testCase in testCases) {
            mIsGettingNextMessageChunkFieldReflection.set(mModel, testCase.isGettingNextMessageChunk)

            setUiState(MateChatUiState(messageChunkSizes = testCase.messageChunkSizes))

            val gottenIsNextMessageChunkGettingAllowed = mModel.isNextMessageChunkGettingAllowed()

            Assert.assertEquals(
                testCase.expectedIsNextMessageChunkGettingAllowed,
                gottenIsNextMessageChunkGettingAllowed
            )
        }
    }

    @Test
    fun resetMessageChunksTest() {
        val initMessages = mutableListOf(DEFAULT_MATE_MESSAGE_PRESENTATION)
        val initMessageChunkSizes = mutableMapOf(0 to initMessages.size)
        val initNewMessageCount = 2
        val initUiState = MateChatUiState(
            messages = initMessages,
            messageChunkSizes = initMessageChunkSizes,
            newMessageCount = initNewMessageCount
        )

        val expectedMessages = listOf<MateMessagePresentation>()
        val expectedMessageChunkSizes = mutableMapOf<Int, Int>()
        val expectedNewMessageCount = 0

        setUiState(initUiState)

        mModel.resetMessageChunks()

        val finalUiState = mModel.uiState

        AssertUtils.assertEqualContent(expectedMessages, finalUiState.messages)
        AssertUtils.assertEqualMaps(expectedMessageChunkSizes, finalUiState.messageChunkSizes)
        Assert.assertEquals(expectedNewMessageCount, finalUiState.newMessageCount)
    }

    @Test
    fun getInterlocutorProfileTest() = runTest {
        val initChatContext = DEFAULT_MATE_CHAT_PRESENTATION
        val initUiState = MateChatUiState(chatContext = initChatContext)

        val expectedUserPresentation = initChatContext.user

        setUiState(initUiState)

        val gottenUserPresentation = mModel.getInterlocutorProfile()

        Assert.assertEquals(expectedUserPresentation, gottenUserPresentation)
    }

    @Test
    fun addInterlocutorAsMateTest() = runTest {
        val initLoadingState = false
        val initChatContext = DEFAULT_MATE_CHAT_PRESENTATION
        val initUiState = MateChatUiState(isLoading = initLoadingState, chatContext = initChatContext)

        val expectedLoadingState = true

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mModel.addInterlocutorAsMate()

            val operation = awaitItem()

            Assert.assertTrue(mSendMateRequestToInterlocutorCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
        }
    }

    @Test
    fun deleteChatTest() = runTest {
        val initLoadingState = false
        val initChatContext = DEFAULT_MATE_CHAT_PRESENTATION
        val initUiState = MateChatUiState(isLoading = initLoadingState, chatContext = initChatContext)

        val expectedLoadingState = true

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mModel.deleteChat()

            val operation = awaitItem()

            Assert.assertTrue(mDeleteChatCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
        }
    }

    @Test
    fun processSendMateRequestToInterlocutorDomainResultWithErrorTest() = runTest {
        val initLoadingState = true
        val initUiState = MateChatUiState(isLoading = initLoadingState)

        val expectedError = TestError.normal
        val expectedLoadingState = false

        val sendMateRequestToInterlocutorDomainResult =
            SendMateRequestDomainResult(error = expectedError)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(sendMateRequestToInterlocutorDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedError, finalUiState.error)
        }
    }

    @Test
    fun processSendMateRequestToInterlocutorDomainResultTest() = runTest {
        val initLoadingState = true
        val initIsMateRequestSendingAllowed = true
        val initUiState = MateChatUiState(
            isLoading = initLoadingState,
            isMateRequestSendingAllowed = initIsMateRequestSendingAllowed
        )

        val expectedLoadingState = false
        val expectedIsMateRequestSendingAllowed = false

        val sendMateRequestToInterlocutorDomainResult = SendMateRequestDomainResult()

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(sendMateRequestToInterlocutorDomainResult)

            val mateRequestSentOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(
                MateRequestSentToInterlocutorUiOperation::class,
                mateRequestSentOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedIsMateRequestSendingAllowed,
                finalUiState.isMateRequestSendingAllowed)
        }
    }

    @Test
    fun processDeleteChatDomainResultWithErrorTest() = runTest {
        val initLoadingState = true
        val initUiState = MateChatUiState(isLoading = initLoadingState)

        val expectedError = TestError.normal
        val expectedLoadingState = false

        val deleteChatDomainResult = DeleteChatDomainResult(error = expectedError)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(deleteChatDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedError, finalUiState.error)
        }
    }

    @Test
    fun processDeleteChatDomainResultTest() = runTest {
        val initLoadingState = true
        val initUiState = MateChatUiState(isLoading = initLoadingState)

        val expectedLoadingState = false

        val deleteChatDomainResult = DeleteChatDomainResult()

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(deleteChatDomainResult)

            val chatDeletedOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ChatDeletedUiOperation::class, chatDeletedOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun processGetInterlocutorDomainResultWithErrorTest() = runTest {
        val initLoadingState = false
        val initChatContext = DEFAULT_MATE_CHAT_PRESENTATION
        val initUiState = MateChatUiState(
            isLoading = initLoadingState, chatContext = initChatContext)

        val expectedError = TestError.normal
        val expectedLoadingState = false

        val getInterlocutorDomainResult = GetUserDomainResult(error = expectedError)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getInterlocutorDomainResult)

            val errorOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedError, finalUiState.error)
        }
    }

    @Test
    fun processGetInterlocutorDomainResultTest() = runTest {
        val initLoadingState = false
        val initChatContext = DEFAULT_MATE_CHAT_PRESENTATION
        val initUiState = MateChatUiState(
            isLoading = initLoadingState, chatContext = initChatContext)

        val user = DEFAULT_USER

        val expectedUserPresentation = user.toUserPresentation()
        val expectedLoadingState = false

        val getInterlocutorDomainResult = GetUserDomainResult(interlocutor = user)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getInterlocutorDomainResult)

            val operation = awaitItem()

            Assert.assertEquals(ShowInterlocutorDetailsUiOperation::class, operation::class)

            val gottenUserPresentation = (operation as ShowInterlocutorDetailsUiOperation).interlocutor
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedUserPresentation, gottenUserPresentation)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedUserPresentation, finalUiState.chatContext!!.user)
        }
    }

    @Test
    fun processUpdateInterlocutorDomainResultWithErrorTest() = runTest {
        val initLoadingState = false
        val initUiState = MateChatUiState(isLoading = initLoadingState)

        val expectedError = TestError.normal
        val expectedLoadingState = false

        val updateInterlocutorDomainResult = UpdateUserDomainResult(error = expectedError)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateInterlocutorDomainResult)

            val errorOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedError, finalUiState.error)
        }
    }

    @Test
    fun processUpdateInterlocutorDomainResultTest() = runTest {
        val initLoadingState = false
        val initChatContext = DEFAULT_MATE_CHAT_PRESENTATION
        val initUiState = MateChatUiState(
            isLoading = initLoadingState, chatContext = initChatContext)

        val user = DEFAULT_USER

        val expectedUserPresentation = user.toUserPresentation()
        val expectedChatContext = initChatContext.copy(user = expectedUserPresentation)
        val expectedLoadingState = false

        val updateInterlocutorDomainResult = UpdateUserDomainResult(interlocutor = user)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateInterlocutorDomainResult)

            val interlocutorOperation = awaitItem()
            val chatContextOperation = awaitItem()

            Assert.assertEquals(UpdateInterlocutorDetailsUiOperation::class,
                interlocutorOperation::class)
            Assert.assertEquals(ChatContextUpdatedUiOperation::class, chatContextOperation::class)

            val gottenUserPresentation = (interlocutorOperation
                as UpdateInterlocutorDetailsUiOperation).interlocutor
            val gottenChatContext = (chatContextOperation as ChatContextUpdatedUiOperation)
                .chatContext
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedUserPresentation, gottenUserPresentation)
            Assert.assertEquals(expectedChatContext, gottenChatContext)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedUserPresentation, finalUiState.chatContext!!.user)
            Assert.assertEquals(expectedChatContext, finalUiState.chatContext)
        }
    }

    @Test
    fun processGetMessageChunkDomainResultWithErrorTest() = runTest {
        val initLoadingState = true
        val initIsGettingNextMessageChunk = true
        val initUiState = MateChatUiState(isLoading = initLoadingState)

        val expectedError = TestError.normal
        val expectedLoadingState = false
        val expectedIsGettingNextMessageChunk = false

        val getMessageChunkDomainResult = GetMessageChunkDomainResult(error = expectedError)

        setUiState(initUiState)
        mIsGettingNextMessageChunkFieldReflection.set(mModel, initIsGettingNextMessageChunk)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getMessageChunkDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenIsGettingNextMessageChunk = mIsGettingNextMessageChunkFieldReflection.get(mModel)
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedIsGettingNextMessageChunk, gottenIsGettingNextMessageChunk)
            Assert.assertEquals(expectedError, finalUiState.error)
        }
    }

    @Test
    fun processGetMessageChunkDomainResultTest() = runTest {
        val initLoadingState = true
        val initIsGettingNextMessageChunk = true
        val initUiState = MateChatUiState(isLoading = initLoadingState)

        val mateMessageChunk = MateMessageChunk(0, listOf(DEFAULT_MATE_MESSAGE))

        val expectedLoadingState = false
        val expectedMateMessageChunk = mateMessageChunk.messages
            .map { it.toMateMessagePresentation() }
        val expectedMessageChunkSizes = mutableMapOf(0 to mateMessageChunk.messages.size)
        val expectedIsGettingNextMessageChunk = false

        val getMessageChunkDomainResult = GetMessageChunkDomainResult(chunk = mateMessageChunk)

        setUiState(initUiState)
        mIsGettingNextMessageChunkFieldReflection.set(mModel, initIsGettingNextMessageChunk)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getMessageChunkDomainResult)

            val insertMessagesOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(InsertMessagesUiOperation::class, insertMessagesOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenMateMessageChunk = (insertMessagesOperation as
                    InsertMessagesUiOperation).messages
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenIsGettingNextMessageChunk = mIsGettingNextMessageChunkFieldReflection.get(mModel)
            val finalUiState = mModel.uiState

            AssertUtils.assertEqualContent(expectedMateMessageChunk, gottenMateMessageChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            AssertUtils.assertEqualMaps(expectedMessageChunkSizes, finalUiState.messageChunkSizes)
            Assert.assertEquals(expectedIsGettingNextMessageChunk, gottenIsGettingNextMessageChunk)
            AssertUtils.assertEqualContent(expectedMateMessageChunk, finalUiState.messages)
        }
    }

    @Test
    fun processUpdateMessageChunkDomainResultWithErrorTest() = runTest {
        val initLoadingState = true
        val initIsGettingNextMessageChunk = true
        val initUiState = MateChatUiState(isLoading = initLoadingState)

        val expectedError = TestError.normal
        val expectedLoadingState = false
        val expectedIsGettingNextMessageChunk = false

        val updateMessageChunkDomainResult = UpdateMessageChunkDomainResult(error = expectedError)

        setUiState(initUiState)
        mIsGettingNextMessageChunkFieldReflection.set(mModel, initIsGettingNextMessageChunk)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateMessageChunkDomainResult)

            val errorOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenError = (errorOperation as ErrorUiOperation).error
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenIsGettingNextMessageChunk = mIsGettingNextMessageChunkFieldReflection.get(mModel)
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedError, gottenError)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedIsGettingNextMessageChunk, gottenIsGettingNextMessageChunk)
            Assert.assertEquals(expectedError, finalUiState.error)
        }
    }

    @Test
    fun processUpdateMessageChunkDomainResultTest() = runTest {
        val initLoadingState = true
        val initIsGettingNextMessageChunk = true
        val initMessages = mutableListOf(
            DEFAULT_MATE_MESSAGE_PRESENTATION,
            DEFAULT_MATE_MESSAGE_PRESENTATION
        )
        val initMessageChunkSizes = mutableMapOf(0 to initMessages.size)
        val initUiState = MateChatUiState(
            isLoading = initLoadingState,
            messages = initMessages,
            messageChunkSizes = initMessageChunkSizes
        )

        val updatedMateMessageChunk = MateMessageChunk(
            0,
            listOf(
                DEFAULT_MATE_MESSAGE.copy(text = "updated text")
            )
        )

        val expectedLoadingState = false
        val expectedUpdatedMateMessageChunk = updatedMateMessageChunk.messages
            .map { it.toMateMessagePresentation() }
        val expectedMessageChunkSizes = initMessageChunkSizes.toMutableMap()
            .apply { this[0] = updatedMateMessageChunk.messages.size }
        val expectedIsGettingNextMessageChunk = false

        val updateMessageChunkDomainResult = UpdateMessageChunkDomainResult(
            chunk = updatedMateMessageChunk)

        setUiState(initUiState)
        mIsGettingNextMessageChunkFieldReflection.set(mModel, initIsGettingNextMessageChunk)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateMessageChunkDomainResult)

            val updateMessageChunkOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(UpdateMessageChunkUiOperation::class, updateMessageChunkOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenUpdatedMateMessageChunk = (updateMessageChunkOperation as
                    UpdateMessageChunkUiOperation).messages
            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenIsGettingNextMessageChunk = mIsGettingNextMessageChunkFieldReflection.get(mModel)
            val finalUiState = mModel.uiState

            AssertUtils.assertEqualContent(expectedUpdatedMateMessageChunk, gottenUpdatedMateMessageChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            Assert.assertEquals(expectedIsGettingNextMessageChunk, gottenIsGettingNextMessageChunk)
            AssertUtils.assertEqualContent(expectedUpdatedMateMessageChunk, finalUiState.messages)
            AssertUtils.assertEqualMaps(expectedMessageChunkSizes, finalUiState.messageChunkSizes)
        }
    }
}