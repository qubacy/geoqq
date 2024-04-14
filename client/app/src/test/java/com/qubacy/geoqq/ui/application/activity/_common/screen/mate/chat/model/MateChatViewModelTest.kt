package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.mate.chat.model.MateMessage
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.toImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

class MateChatViewModelTest(

) : BusinessViewModelTest<MateChatUiState, MateChatUseCase, MateChatViewModel>(
    MateChatUseCase::class.java
) {
    companion object {
        val DEFAULT_IMAGE = Image(0, UriMockUtil.getMockedUri())
        val DEFAULT_USER = User(
            0,
            "test", "test",
            DEFAULT_IMAGE,
            false, false
        )
        val DEFAULT_MATE_MESSAGE = MateMessage(0, DEFAULT_USER, "test", 0)

        val DEFAULT_IMAGE_PRESENTATION = DEFAULT_IMAGE.toImagePresentation()
        val DEFAULT_USER_PRESENTATION = DEFAULT_USER.toUserPresentation()
        val DEFAULT_MATE_CHAT_PRESENTATION = MateChatPresentation(
            0, DEFAULT_USER_PRESENTATION, 0, null
        )
        val DEFAULT_MATE_MESSAGE_PRESENTATION = DEFAULT_MATE_MESSAGE.toMateMessagePresentation()
    }

    private lateinit var mLastMessageChunkIndexFieldReflection: Field
    private lateinit var mIsGettingNextMessageChunkFieldReflection: Field

    private var mGetMessageChunkCallFlag = false
    private var mGetInterlocutorCallFlag = false
    private var mSendMateRequestToInterlocutorCallFlag = false
    private var mDeleteChatCallFlag = false

    override fun preInit() {
        super.preInit()

        mLastMessageChunkIndexFieldReflection = MateChatViewModel::class.java
            .getDeclaredField("mLastMessageChunkIndex")
            .apply { isAccessible = true }
        mIsGettingNextMessageChunkFieldReflection = MateChatViewModel::class.java
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
            Mockito.anyLong(), Mockito.anyInt()
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
        errorDataRepository: ErrorDataRepository
    ): MateChatViewModel {
        return MateChatViewModel(savedStateHandle, errorDataRepository, mUseCase)
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
            val chatContext = DEFAULT_MATE_CHAT_PRESENTATION.copy(user = userPresentation)

            setUiState(MateChatUiState(chatContext = chatContext))

            val gottenIsChatable = mModel.isInterlocutorChatable()

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
            val userPresentation = DEFAULT_USER_PRESENTATION.copy(
                isMate = testCase.isUserMate)
            val chatContext = DEFAULT_MATE_CHAT_PRESENTATION.copy(user = userPresentation)

            setUiState(MateChatUiState(
                chatContext = chatContext,
                isMateRequestSendingAllowed = testCase.isMateRequestSendingAllowed
            ))

            val gottenIsMateable = mModel.isInterlocutorMateable()

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
            val userPresentation = DEFAULT_USER_PRESENTATION.copy(
                isMate = testCase.isUserMate)
            val chatContext = DEFAULT_MATE_CHAT_PRESENTATION.copy(user = userPresentation)

            setUiState(MateChatUiState(
                chatContext = chatContext,
                isMateRequestSendingAllowed = testCase.isMateRequestSendingAllowed
            ))

            val gottenIsMateableOrDeletable = mModel.isInterlocutorMateableOrDeletable()

            Assert.assertEquals(testCase.expectedIsMateableOrDeletable, gottenIsMateableOrDeletable)
        }
    }

    @Test
    fun isNextMessageChunkGettingAllowedTest() {

    }

    @Test
    fun getInterlocutorProfileTest() {

    }

    @Test
    fun addInterlocutorAsMateTest() {

    }

    @Test
    fun deleteChatTest() {

    }

    @Test
    fun processSendMateRequestToInterlocutorDomainResultTest() {

    }

    @Test
    fun processDeleteChatDomainResultTest() {

    }

    @Test
    fun processGetInterlocutorDomainResultTest() {

    }

    @Test
    fun processUpdateInterlocutorDomainResultTest() {

    }

    @Test
    fun processGetMessageChunkDomainResultTest() {

    }

    @Test
    fun processUpdateMessageChunkDomainResultTest() {

    }
}