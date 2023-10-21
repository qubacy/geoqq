package com.qubacy.geoqq.ui.screen.mate.chat.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.chat.operation.AddMessageChatOperation
import com.qubacy.geoqq.data.common.chat.operation.AddUserChatOperation
import com.qubacy.geoqq.data.common.chat.state.ChatState
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.chat.message.validator.MessageTextValidator
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.BaseViewModel
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddUserUiOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class MateChatViewModel(

) : BaseViewModel() {
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

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // todo: sending a message using DATA layer..

            mMateChatStateFlow.emit(ChatState(
                listOf(Message(0,0, text, 1697448075990)),
                listOf(User(0, "me")),
                listOf(AddMessageChatOperation(0))
            ))
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

        return ChatUiState(chatState.messages, chatState.users, uiOperations)
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
}

class MateChatViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatViewModel() as T
    }
}