package com.qubacy.geoqq.ui.screen.mate.chat.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.chat.operation.AddMessageChatOperation
import com.qubacy.geoqq.data.common.chat.operation.AddUserChatOperation
import com.qubacy.geoqq.data.common.chat.operation.ChangeChatInfoOperation
import com.qubacy.geoqq.data.common.chat.state.ChatState
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.chat.message.validator.MessageTextValidator
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.mates.chat.entity.MateChat
import com.qubacy.geoqq.ui.common.fragment.common.base.model.BaseViewModel
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddUserUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.ChangeChatInfoUiOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class MateChatViewModel(
    val chatId: Long
) : BaseViewModel(), ChatViewModel {
    // todo: assign to the repository's flow:
    private val mMateChatStateFlow = MutableStateFlow<ChatState?>(null)

    private val mMateChatUiStateFlow = mMateChatStateFlow.map { chatStateToUiState(it) }
    val mateChatUiStateFlow: LiveData<ChatUiState?> = mMateChatUiStateFlow.asLiveData()

    fun isMessageCorrect(text: String): Boolean {
        if (!isMessageFull(text)) return false

        return MessageTextValidator().check(text)
    }

    private fun isMessageFull(text: String): Boolean {
        return text.isNotEmpty()
    }

    // todo: delete:
    private var curMessageId = 0L

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // todo: sending a message using DATA layer..

            // todo: delete:
            val newMessages = mutableListOf<Message>()
            val messages = if (mateChatUiStateFlow.value != null) mateChatUiStateFlow.value!!.messages else listOf()

            newMessages.addAll(messages)
            newMessages.add(Message(curMessageId,0, text, 1697448075990))

            mMateChatStateFlow.emit(ChatState(
                MateChat(chatId, null, "somebody"),
                newMessages,
                listOf(User(0, "me")),
                listOf(AddMessageChatOperation(curMessageId))
            ))

            ++curMessageId
        }
    }

    private fun chatStateToUiState(chatState: ChatState?): ChatUiState? {
        if (chatState == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in chatState.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return ChatUiState(chatState.chat, chatState.messages, chatState.users, uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            AddUserChatOperation::class -> {
                val addUserChatOperation = operation as AddUserChatOperation

                // mb processing the operation..

                AddUserUiOperation(addUserChatOperation.userId)
            }
            AddMessageChatOperation::class -> {
                val addMessageOperation = operation as AddMessageChatOperation

                // mb processing the operation..

                AddMessageUiOperation(addMessageOperation.messageId)
            }
            ChangeChatInfoOperation::class -> {
                val changeChatInfoOperation = operation as ChangeChatInfoOperation

                // ...

                ChangeChatInfoUiOperation()
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                // mb processing the operation..

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun getMateInfo(): User {
        // todo: getting the mate's info in the DATA layer..

        return User(0, "somebody", "its fucking me...")
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

class MateChatViewModelFactory(val chatId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatViewModel(chatId) as T
    }
}