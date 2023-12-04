package com.qubacy.geoqq.ui.screen.geochat.chat.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.domain.common.operation.chat.AddMessageChatOperation
import com.qubacy.geoqq.data.common.entity.chat.message.validator.MessageTextValidator
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.chat.ChatState
import com.qubacy.geoqq.domain.geochat.chat.GeoChatUseCase
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.common.fragment.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.common.fragment.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.geochat.chat.model.operation.AddUserUiOperation
import com.qubacy.geoqq.ui.screen.geochat.chat.model.state.GeoChatUiState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class GeoChatViewModel(
    val radius: Int,
    val geoChatUseCase: GeoChatUseCase
) : LocationViewModel(), ChatViewModel {
    private val mGeoChatStateFlow = geoChatUseCase.stateFlow

    private val mGeoChatUiStateFlow = mGeoChatStateFlow.map { chatStateToUiState(it) }
    val geoChatUiStateFlow: LiveData<ChatUiState?> = mGeoChatUiStateFlow.asLiveData()

    override fun changeLastLocation(location: Location): Boolean {
        if (!super.changeLastLocation(location)) return false

        // todo: calling some data-layer methods..??

        return true
    }

    fun isMessageCorrect(text: String): Boolean {
        if (!isMessageFull(text)) return false

        return MessageTextValidator().check(text)
    }

    private fun isMessageFull(text: String): Boolean {
        return text.isNotEmpty()
    }

    fun getGeoChat() {
        val lastLocationPoint = lastLocationPoint.value ?: return

        mIsWaiting.value = true

        geoChatUseCase.getGeoChat(radius, lastLocationPoint.latitude, lastLocationPoint.longitude)
    }

    fun sendMessage(text: String) {
        // todo: sending a message...


    }

    private fun chatStateToUiState(chatState: ChatState?): GeoChatUiState? {
        if (chatState == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in chatState.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return GeoChatUiState(chatState.messages, chatState.users, uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            Set
            AddUserChatOperation::class -> { // todo: is it necessary?
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

    fun addToMates(user: User) {
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

open class GeoChatViewModelFactory(
    val radius: Int,
    val geoChatUseCase: GeoChatUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatViewModel(radius, geoChatUseCase) as T
    }
}