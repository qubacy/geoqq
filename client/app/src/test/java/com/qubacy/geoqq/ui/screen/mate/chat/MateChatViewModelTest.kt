package com.qubacy.geoqq.ui.screen.mate.chat

import android.net.Uri
import app.cash.turbine.test
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.SetMessagesUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.ChatViewModelTest
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chat.model.state.MateChatUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MateChatViewModelTest() : ChatViewModelTest<MateChatState, MateChatUiState>() {
    companion object {
        init {
            UriMockContext.mockUri()
        }
    }

    override fun generateChatState(
        messages: List<Message>,
        users: List<User>,
        operations: List<Operation>
    ): MateChatState {
        return MateChatState(messages, users, operations)
    }

    override fun initChatViewModel(newState: MateChatState?) {
        val mateChatUseCastMock = Mockito.mock(MateChatUseCase::class.java)

        Mockito.`when`(mateChatUseCastMock.getChat(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt())
        ).thenAnswer { setNewUiState(newState) }
        Mockito.`when`(mateChatUseCastMock.getInterlocutorUserDetails())
            .thenAnswer { setNewUiState(newState) }
        Mockito.`when`(mateChatUseCastMock.createMateRequest(Mockito.anyLong()))
            .thenAnswer { setNewUiState(newState) }
        Mockito.`when`(mateChatUseCastMock.sendMessage(Mockito.anyLong(), Mockito.anyString()))
            .thenAnswer {  }

        mChatStateFlow = MutableStateFlow<MateChatState?>(null)

        Mockito.`when`(mateChatUseCastMock.stateFlow).thenAnswer {
            mChatStateFlow
        }

        val mMateChatUiStateFlowFieldReflection = MateChatViewModel::class.java
            .getDeclaredField("mMateChatUiStateFlow")
            .apply { isAccessible = true }

        mModel = MateChatViewModel(0L, 1L, mateChatUseCastMock)
        mChatUiStateFlow = mMateChatUiStateFlowFieldReflection.get(mModel) as Flow<MateChatUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initChatViewModel()
    }

    @Test
    fun getChatTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MateChatState(
            listOf(
                Message(0, 0, "test 1", 100L),
                Message(1, 1, "test 2", 100L)
            ),
            listOf(
                User(0, "me", "pox", mockUri, true),
                User(1, "test", "pox", mockUri, true)
            ),
            listOf(
                SetMessagesOperation()
            )
        )

        initChatViewModel(newState)

        mChatUiStateFlow.test {
            awaitItem()
            (mModel as MateChatViewModel).getMessages()

            val gottenState = awaitItem()!!

            for (sourceMessage in newState.messages)
                Assert.assertNotNull(gottenState.messages.find { it == sourceMessage })
            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMessagesUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }

    @Test
    fun sendMessageTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MateChatState(
            listOf(),
            listOf(
                User(0, "me", "pox", mockUri, true),
                User(1, "test", "pox", mockUri, true)
            ),
            listOf()
        )

        initChatViewModel(newState)

        mChatUiStateFlow.test {
            awaitItem()
            (mModel as MateChatViewModel).sendMessage("test")
        }
    }
}