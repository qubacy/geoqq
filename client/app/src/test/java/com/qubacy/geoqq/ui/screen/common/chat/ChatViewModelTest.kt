package com.qubacy.geoqq.ui.screen.common.chat

import app.cash.turbine.test
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.chat.ApproveNewMateRequestCreationOperation
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.chat.ChatState
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.MateRequestCreatedUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.OpenUserDetailsUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

abstract class ChatViewModelTest<StateType : ChatState, UiStateType: ChatUiState> : ViewModelTest() {
    protected lateinit var mModel: ChatViewModel
    protected lateinit var mChatStateFlow: MutableStateFlow<StateType?>

    protected lateinit var mChatUiStateFlow: Flow<UiStateType?>

    protected abstract fun generateChatState(
        messages: List<Message>,
        users: List<User>,
        operations: List<Operation>
    ): StateType
    protected abstract fun initChatViewModel(newState: StateType? = null)

    protected fun setNewUiState(newState: StateType?) = runTest {
        if (newState == null) return@runTest

        mChatStateFlow.emit(newState)
    }

    @Test
    fun getMateUserDetailsTest() = runTest {
        val newState = generateChatState(
            listOf(),
            UserGeneratorUtility.generateUsers(2),
            listOf(
                SetUsersDetailsOperation(listOf(1L), false)
            )
        )

        initChatViewModel(newState)

        mChatUiStateFlow.test {
            awaitItem()
            mModel.getUserDetails(1)

            val gottenState = awaitItem()!!

            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(OpenUserDetailsUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }

    @Test
    fun createMateRequestTest() = runTest {
        val newState = generateChatState(
            listOf(),
            UserGeneratorUtility.generateUsers(2),
            listOf(
                ApproveNewMateRequestCreationOperation()
            )
        )

        initChatViewModel(newState)

        mChatUiStateFlow.test {
            awaitItem()
            mModel.createMateRequest(1L)

            val gottenState = awaitItem()!!

            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(MateRequestCreatedUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }
}