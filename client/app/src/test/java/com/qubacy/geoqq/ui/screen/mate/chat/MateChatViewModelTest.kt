package com.qubacy.geoqq.ui.screen.mate.chat

import android.net.Uri
import app.cash.turbine.test
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation
import com.qubacy.geoqq.domain.common.operation.chat.ApproveNewMateRequestCreationOperation
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.common.fragment.chat.model.operation.OpenUserDetailsUiOperation
import com.qubacy.geoqq.ui.common.fragment.chat.model.operation.SetMessagesUiOperation
import com.qubacy.geoqq.ui.screen.geochat.chat.model.operation.MateRequestCreatedUiOperation
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chat.model.state.MateChatUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MateChatViewModelTest() : ViewModelTest() {
    companion object {
        init {
            UriMockContext.mockUri()
        }
    }

    private lateinit var mModel: MateChatViewModel
    private lateinit var mMateChatStateFlow: MutableStateFlow<MateChatState?>

    private lateinit var mMateChatUiStateFlow: Flow<MateChatUiState?>

    private fun setNewUiState(newState: MateChatState?) = runTest {
        if (newState == null) return@runTest

        mMateChatStateFlow.emit(newState)
    }

    private fun initMateChatViewModel(
        newState: MateChatState? = null
    ) {
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

        mMateChatStateFlow = MutableStateFlow<MateChatState?>(null)

        Mockito.`when`(mateChatUseCastMock.stateFlow).thenAnswer {
            mMateChatStateFlow
        }

        val mMateChatUiStateFlowFieldReflection = MateChatViewModel::class.java
            .getDeclaredField("mMateChatUiStateFlow")
            .apply { isAccessible = true }

        mModel = MateChatViewModel(0L, 1L, mateChatUseCastMock)
        mMateChatUiStateFlow = mMateChatUiStateFlowFieldReflection.get(mModel) as Flow<MateChatUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initMateChatViewModel()
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

        initMateChatViewModel(newState)

        mMateChatUiStateFlow.test {
            awaitItem()
            mModel.getMessages()

            val gottenState = awaitItem()!!

            for (sourceMessage in newState.messages)
                Assert.assertNotNull(gottenState.messages.find { it == sourceMessage })
            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMessagesUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }

    @Test
    fun getMateUserDetailsTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MateChatState(
            listOf(),
            listOf(
                User(0, "me", "pox", mockUri, true),
                User(1, "test", "pox", mockUri, true)
            ),
            listOf(
                SetUsersDetailsOperation(listOf(1L), false)
            )
        )

        initMateChatViewModel(newState)

        mMateChatUiStateFlow.test {
            awaitItem()
            mModel.getMateUserDetails()

            val gottenState = awaitItem()!!

            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(OpenUserDetailsUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }

    @Test
    fun createMateRequestTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MateChatState(
            listOf(),
            listOf(
                User(0, "me", "pox", mockUri, true),
                User(1, "test", "pox", mockUri, true)
            ),
            listOf(
                ApproveNewMateRequestCreationOperation()
            )
        )

        initMateChatViewModel(newState)

        mMateChatUiStateFlow.test {
            awaitItem()
            mModel.createMateRequest(1L)

            val gottenState = awaitItem()!!

            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(MateRequestCreatedUiOperation::class, gottenState.takeUiOperation()!!::class)
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

        initMateChatViewModel(newState)

        mMateChatUiStateFlow.test {
            awaitItem()
            mModel.sendMessage("test")
        }
    }
}