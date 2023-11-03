package com.qubacy.geoqq.ui.screen.common.chat

import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.data.common.chat.operation.AddMessageChatOperation
import com.qubacy.geoqq.data.common.chat.operation.AddUserChatOperation
import com.qubacy.geoqq.data.common.chat.operation.ChangeChatInfoOperation
import com.qubacy.geoqq.data.common.chat.state.ChatState
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class ChatFragmentTest {
    class ChatUiStateTestData(
        private val mAdapter: ChatAdapter,
        private val mMateChatStateFlow: MutableStateFlow<ChatState?>,
        private val mMateUiState: LiveData<ChatUiState?>
    ) {
        fun setChat(chat: Chat, messages: List<Message>, users: List<User>) {
            val chatState = ChatState(chat, messages, users, listOf())

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }

            mAdapter.setItems(messages)
        }

        fun addMessage(message: Message, emptyChat: Chat = Chat()) {
            val newMessages = mutableListOf<Message>()

            if (mMateUiState.value != null)
                newMessages.addAll(mMateUiState.value!!.messages)

            newMessages.add(message)

            val chat = if (mMateUiState.value == null) emptyChat else mMateUiState.value!!.chat
            val users = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.users

            val operations = listOf(
                AddMessageChatOperation(message.messageId)
            )

            val chatState = ChatState(chat, newMessages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }

        fun addUser(user: User, emptyChat: Chat = Chat()) {
            val newUsers = mutableListOf<User>()

            if (mMateUiState.value != null)
                newUsers.addAll(mMateUiState.value!!.users)

            newUsers.add(user)

            val chat = if (mMateUiState.value == null) emptyChat else mMateUiState.value!!.chat
            val messages = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.messages

            val operations = listOf(
                AddUserChatOperation(user.userId)
            )

            val chatState = ChatState(chat, messages, newUsers, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }

        fun changeChat(chat: Chat) {
            val users = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.users
            val messages = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.messages

            val operations = listOf(
                ChangeChatInfoOperation()
            )

            val chatState = ChatState(chat, messages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }

        fun showError(error: Error, emptyChat: Chat = Chat()) {
            val chat = if (mMateUiState.value == null) emptyChat else mMateUiState.value!!.chat
            val users = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.users
            val messages = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.messages

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val chatState = ChatState(chat, messages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }
    }
}