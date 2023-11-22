package com.qubacy.geoqq.ui.screen.common.chat

import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.operation.chat.AddMessageChatOperation
import com.qubacy.geoqq.data.common.chat.operation.AddUserChatOperation
import com.qubacy.geoqq.domain.mate.chat.operation.ChangeChatInfoOperation
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class ChatFragmentTest<StateType> {

    abstract class ChatUiStateTestData<StateType>(
        private val mAdapter: ChatAdapter,
        private val mChatStateFlow: MutableStateFlow<StateType?>,
        private val mChatUiState: LiveData<ChatUiState?>
    ) {
        abstract fun generateChatState(
            messages: List<Message>, users: List<User>, operations: List<Operation>): StateType

        fun setChat(chat: Chat, messages: List<Message>, users: List<User>) {
            val chatState = generateChatState(messages, users, listOf()) //MateChatState(messages, users, listOf())

            runBlocking {
                mChatStateFlow.emit(chatState)
            }

            mAdapter.setItems(messages)
        }

        fun addMessage(message: Message, emptyChat: Chat = Chat()) {
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

        fun addUser(user: User, emptyChat: Chat = Chat()) {
            val newUsers = mutableListOf<User>()

            if (mChatUiState.value != null)
                newUsers.addAll(mChatUiState.value!!.users)

            newUsers.add(user)

            val messages = if (mChatUiState.value == null) listOf() else mChatUiState.value!!.messages

            val operations = listOf(
                AddUserChatOperation(user.id)
            )

            val chatState = generateChatState(messages, newUsers, operations)//MateChatState(messages, newUsers, operations)

            runBlocking {
                mChatStateFlow.emit(chatState)
            }
        }

        fun changeChat(chat: Chat) {
            val users = if (mChatUiState.value == null) listOf() else mChatUiState.value!!.users
            val messages = if (mChatUiState.value == null) listOf() else mChatUiState.value!!.messages

            val operations = listOf(
                ChangeChatInfoOperation()
            )

            val chatState = generateChatState(messages, users, operations)//MateChatState(messages, users, operations)

            runBlocking {
                mChatStateFlow.emit(chatState)
            }
        }

        fun showError(error: Error, emptyChat: Chat = Chat()) {
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