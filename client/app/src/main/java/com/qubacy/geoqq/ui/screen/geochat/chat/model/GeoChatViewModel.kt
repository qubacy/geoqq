package com.qubacy.geoqq.ui.screen.geochat.chat.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.chat.operation.AddMessageChatOperation
import com.qubacy.geoqq.data.common.chat.operation.AddUserChatOperation
import com.qubacy.geoqq.data.common.chat.operation.ChangeChatInfoOperation
import com.qubacy.geoqq.data.common.chat.state.ChatState
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.chat.message.validator.MessageTextValidator
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddUserUiOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class GeoChatViewModel(

) : LocationViewModel(), ChatViewModel {
    // todo: assign to the repository's flow:
    private val mGeoChatStateFlow = MutableStateFlow<ChatState?>(null)

    private val mGeoChatUiStateFlow = mGeoChatStateFlow.map { chatStateToUiState(it) }
    val geoChatUiStateFlow: LiveData<ChatUiState?> = mGeoChatUiStateFlow.asLiveData()

    override fun changeLastLocation(location: Location): Boolean {
        if (!super.changeLastLocation(location)) return false

        // todo: calling some data-layer methods..

        return true
    }

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
            val messages = if (geoChatUiStateFlow.value != null) geoChatUiStateFlow.value!!.messages else listOf()

            newMessages.addAll(messages)
            newMessages.add(Message(curMessageId,0, text, 1697448075990))

            mGeoChatStateFlow.emit(ChatState(
                Chat(),
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

                // todo: think of a possible application of this operation:
                //ChangeChatInfoUiOperation()
                null
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

    fun addToFriend(user: User) {
        viewModelScope.launch {
            // todo: conveying a request to the DATA layer..


        }
    }

    fun isLocalUser(userId: Long): Boolean {
        // todo: checking the userId using the DATA layer..

        return false
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

class GeoChatViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatViewModel() as T
    }
}