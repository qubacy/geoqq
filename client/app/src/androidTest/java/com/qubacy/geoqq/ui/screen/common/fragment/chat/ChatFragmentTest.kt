package com.qubacy.geoqq.ui.screen.common.fragment.chat

import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.operation.chat.AddMessageChatOperation
import com.qubacy.geoqq.domain.mate.chat.operation.ChangeChatInfoOperation
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.mate.chat.operation.SetMessagesOperation
import com.qubacy.geoqq.ui.screen.common.ScreenContext
import com.qubacy.geoqq.ui.common.fragment.chat.model.state.ChatUiState
import com.qubacy.geoqq.common.ApplicationTestBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class ChatFragmentTest<StateType> : ApplicationTestBase() {
    companion object {
        val TEST_USERS = ScreenContext.generateTestUsers(2, true)
    }

    abstract class ChatUiStateTestData<StateType>(
        protected val mChatStateFlow: MutableStateFlow<StateType?>,
        protected val mChatUiState: LiveData<ChatUiState?>
    ) {
        abstract fun generateChatState(
            messages: List<Message>, users: List<User>, operations: List<Operation>): StateType

        fun setChat(messages: List<Message>, users: List<User>) {
            val chatState = generateChatState(messages, users, listOf(SetMessagesOperation()))

            runBlocking {
                mChatStateFlow.emit(chatState)
            }
        }

        fun addMessage(message: Message) {
            val newMessages = mutableListOf<Message>()

            if (mChatUiState.value != null)
                newMessages.addAll(mChatUiState.value!!.messages)

            newMessages.add(message)

            val users = if (mChatUiState.value == null) listOf() else mChatUiState.value!!.users

            val operations = listOf(
                AddMessageChatOperation(message.id)
            )

            val chatState = generateChatState(newMessages, users, operations)//MateChatState(newMessages, users, operations)

            runBlocking {
                mChatStateFlow.emit(chatState)
            }
        }

        // TODO: move addUser() to GeoChatFragmentTest!!

//        fun addUser(user: User) {
//            val newUsers = mutableListOf<User>()
//
//            if (mChatUiState.value != null)
//                newUsers.addAll(mChatUiState.value!!.users)
//
//            newUsers.add(user)
//
//            val messages = if (mChatUiState.value == null) listOf() else mChatUiState.value!!.messages
//
//            val operations = listOf(
//                AddUserChatOperation(user.id)
//            )
//
//            val chatState = generateChatState(messages, newUsers, operations)//MateChatState(messages, newUsers, operations)
//
//            runBlocking {
//                mChatStateFlow.emit(chatState)
//            }
//        }

        fun changeChat(lastMessage: Message) {
            val users = mChatUiState.value?.users ?: listOf()
            val messages = mChatUiState.value?.messages?.toMutableList()?.apply { add(lastMessage) }
                ?: listOf()

            val operations = listOf(
                ChangeChatInfoOperation()
            )

            val chatState = generateChatState(messages, users, operations)//MateChatState(messages, users, operations)

            runBlocking {
                mChatStateFlow.emit(chatState)
            }
        }

        fun showError(error: Error) {
            val users = if (mChatUiState.value == null) listOf() else mChatUiState.value!!.users
            val messages = if (mChatUiState.value == null) listOf() else mChatUiState.value!!.messages

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val chatState = generateChatState(messages, users, operations)//MateChatState(messages, users, operations)

            runBlocking {
                mChatStateFlow.emit(chatState)
            }
        }
    }
}