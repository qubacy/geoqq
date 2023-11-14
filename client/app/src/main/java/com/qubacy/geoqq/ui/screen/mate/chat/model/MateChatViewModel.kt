package com.qubacy.geoqq.ui.screen.mate.chat.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.domain.common.operation.chat.AddMessageChatOperation
import com.qubacy.geoqq.domain.mate.chat.operation.ChangeChatInfoOperation
import com.qubacy.geoqq.data.common.entity.chat.message.validator.MessageTextValidator
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.operation.SetUserDetailsOperation
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.AddUserUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.ChangeChatInfoUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.ChangeUserUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.operation.OpenUserDetailsUiOperation
import com.qubacy.geoqq.ui.screen.mate.chat.model.state.MateChatUiState
import kotlinx.coroutines.flow.map

class MateChatViewModel(
    val chatId: Long,
    val interlocutorUserId: Long,
    val mateChatUseCase: MateChatUseCase
) : WaitingViewModel(), ChatViewModel {
    companion object {
        const val DEFAULT_MESSAGE_CHUNK_SIZE = 20
    }

    private val mMateChatStateFlow = mateChatUseCase.stateFlow

    private val mMateChatUiStateFlow = mMateChatStateFlow.map { chatStateToUiState(it) }
    val mateChatUiStateFlow: LiveData<MateChatUiState?> = mMateChatUiStateFlow.asLiveData()

    private var mIsWaitingForInterlocutorDetails = true

    init {
        mateChatUseCase.setCoroutineScope(viewModelScope)
    }

    fun isMessageCorrect(text: String): Boolean {
        if (!isMessageFull(text)) return false

        return MessageTextValidator().check(text)
    }

    private fun isMessageFull(text: String): Boolean {
        return text.isNotEmpty()
    }

    fun sendMessage(text: String) {
        mateChatUseCase.sendMessage(chatId, text)
    }

    fun getMessages() {
        mIsWaiting.value = true

        mateChatUseCase.getChat(chatId, interlocutorUserId, DEFAULT_MESSAGE_CHUNK_SIZE)
    }

    fun getMateUserDetails() {
        mIsWaiting.value = true

        mateChatUseCase.getInterlocutorUserDetails()
    }

    private fun chatStateToUiState(chatState: MateChatState?): MateChatUiState? {
        if (mIsWaiting.value == true) mIsWaiting.value = false
        if (chatState == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in chatState.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        val title = chatState.users.find {it.id == interlocutorUserId}?.username ?: String()

        return MateChatUiState(title, chatState.messages, chatState.users, uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            AddMessageChatOperation::class -> {
                val addMessageOperation = operation as AddMessageChatOperation

                // mb processing the operation..

                AddMessageUiOperation(addMessageOperation.messageId)
            }
            SetUserDetailsOperation::class -> {
                val setUserDetailsOperation = operation as SetUserDetailsOperation

                processSetUserDetailsOperation(setUserDetailsOperation)
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

    private fun processSetUserDetailsOperation(
        setUserDetailsOperation: SetUserDetailsOperation
    ): UiOperation? {
        if (mIsWaitingForInterlocutorDetails
            && setUserDetailsOperation.userId == interlocutorUserId
        ) {
            mIsWaitingForInterlocutorDetails = false

            return OpenUserDetailsUiOperation(setUserDetailsOperation.userId)
        }

        return ChangeUserUiOperation(setUserDetailsOperation.userId)
    }

    fun getMateInfo(): User {
        val user = mateChatUiStateFlow.value!!.users.find { it.id == interlocutorUserId }!!

        return user
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

class MateChatViewModelFactory(
    val chatId: Long,
    val interlocutorUserId: Long,
    val mateChatUseCase: MateChatUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatViewModel(chatId, interlocutorUserId, mateChatUseCase) as T
    }
}