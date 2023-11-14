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
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class ChatFragmentTest {
    class ChatUiStateTestData(
        private val mAdapter: ChatAdapter,
        private val mMateChatStateFlow: MutableStateFlow<MateChatState?>,
        private val mMateUiState: LiveData<ChatUiState?>
    ) {
        fun setChat(chat: Chat, messages: List<Message>, users: List<User>) {
            val chatState = MateChatState(messages, users, listOf())

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

            val users = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.users

            val operations = listOf(
                AddMessageChatOperation(message.id)
            )

            val chatState = MateChatState(newMessages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }

        fun addUser(user: User, emptyChat: Chat = Chat()) {
            val newUsers = mutableListOf<User>()

            if (mMateUiState.value != null)
                newUsers.addAll(mMateUiState.value!!.users)

            newUsers.add(user)

            val messages = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.messages

            val operations = listOf(
                AddUserChatOperation(user.id)
            )

            val chatState = MateChatState(messages, newUsers, operations)

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

            val chatState = MateChatState(messages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }

        fun showError(error: Error, emptyChat: Chat = Chat()) {
            val users = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.users
            val messages = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.messages

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val chatState = MateChatState(messages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }
    }
}