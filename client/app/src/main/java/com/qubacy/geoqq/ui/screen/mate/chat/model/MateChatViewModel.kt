package com.qubacy.geoqq.ui.screen.mate.chat.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.domain.common.operation.chat.AddMessageChatOperation
import com.qubacy.geoqq.domain.mate.chat.operation.ChangeChatInfoOperation
import com.qubacy.geoqq.data.common.model.message.validator.MessageTextValidator
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.mate.chat.operation.AddPrecedingMessagesOperation
import com.qubacy.geoqq.domain.common.operation.chat.ApproveNewMateRequestCreationOperation
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.ChangeChatInfoUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.ChangeUsersUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.OpenUserDetailsUiOperation
import com.qubacy.geoqq.ui.screen.mate.chat.model.state.MateChatUiState
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.SetMessagesUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.MateRequestCreatedUiOperation
import com.qubacy.geoqq.ui.screen.mate.chat.model.operation.AddPrecedingMessagesUiOperation
import kotlinx.coroutines.flow.map

open class MateChatViewModel(
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

    private var mIsWaitingForInterlocutorDetails = false

    private var mIsGettingChat: Boolean = false
    val isGettingChat get() = mIsGettingChat

    private var mIsGettingNewChunk = false

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
        mIsGettingChat = true

        mateChatUseCase.getChat(chatId, interlocutorUserId, DEFAULT_MESSAGE_CHUNK_SIZE)
    }

    override fun getUserDetails(userId: Long) {
        mIsWaiting.value = true
        mIsWaitingForInterlocutorDetails = true

        mateChatUseCase.getInterlocutorUserDetails() // todo: this is ugly
    }

    fun getMateUserDetails() {
        getUserDetails(interlocutorUserId)
    }

    fun messageListEndReached() {
        val curState = mateChatUiStateFlow.value

        if (curState == null || mIsGettingNewChunk) return

        mIsGettingNewChunk = true

        val curMessageCount = curState.messages.size
        val nextMessageCount = curMessageCount + DEFAULT_MESSAGE_CHUNK_SIZE

        if (nextMessageCount % DEFAULT_MESSAGE_CHUNK_SIZE != 0) return

        mateChatUseCase.getChat(chatId, interlocutorUserId, nextMessageCount)
    }

    override fun createMateRequest(userId: Long) {
        mIsWaiting.value = true

        mateChatUseCase.createMateRequest(userId)
    }

    private fun chatStateToUiState(chatState: MateChatState?): MateChatUiState? {
        if (chatState == null) return null

        val uiOperationsResult = mutableListOf<UiOperation>()

        for (operation in chatState.newOperations) {
            val uiOperations = processOperation(operation)

            if (uiOperations.isEmpty()) continue

            uiOperationsResult.addAll(uiOperations)
        }

        val title = chatState.users.find {it.id == interlocutorUserId}?.username ?: String()

        return MateChatUiState(title, chatState.messages, chatState.users, uiOperationsResult)
    }

    private fun processOperation(operation: Operation): List<UiOperation> {
        return when (operation::class) {
            SetMessagesOperation::class -> {
                val setMessagesOperation = operation as SetMessagesOperation

                mIsWaiting.value = false

                listOf(SetMessagesUiOperation())
            }
            AddMessageChatOperation::class -> {
                val addMessageOperation = operation as AddMessageChatOperation

                // mb processing the operation..

                listOf(AddMessageUiOperation(addMessageOperation.messageId))
            }
            AddPrecedingMessagesOperation::class -> {
                val addPrecedingMessagesOperation = operation as AddPrecedingMessagesOperation

                mIsGettingNewChunk = false

                listOf(AddPrecedingMessagesUiOperation(addPrecedingMessagesOperation.precedingMessages))
            }
            SetUsersDetailsOperation::class -> {
                val setUsersDetailsOperation = operation as SetUsersDetailsOperation

                processSetUsersDetailsOperation(setUsersDetailsOperation)
            }
            ChangeChatInfoOperation::class -> {
                val changeChatInfoOperation = operation as ChangeChatInfoOperation

                // ...

                listOf(ChangeChatInfoUiOperation())
            }
            ApproveNewMateRequestCreationOperation::class -> {
                val approveNewMateRequestCreationOperation =
                    operation as ApproveNewMateRequestCreationOperation

                mIsWaiting.value = false

                listOf(MateRequestCreatedUiOperation())
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                mIsWaiting.value = false

                listOf(ShowErrorUiOperation(handleErrorOperation.error))
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    private fun processSetUsersDetailsOperation(
        setUsersDetailsOperation: SetUsersDetailsOperation
    ): List<UiOperation> {
        mIsWaiting.value = false

        val operations = mutableListOf<UiOperation>()

        if (setUsersDetailsOperation.areUpdated)
            operations.add(ChangeUsersUiOperation(setUsersDetailsOperation.usersIds))

        if (mIsWaitingForInterlocutorDetails
            && setUsersDetailsOperation.usersIds.find { it == interlocutorUserId } != null
        ) {
            mIsWaitingForInterlocutorDetails = false

            operations.add(OpenUserDetailsUiOperation(interlocutorUserId))
        }

        return operations
    }

    fun getMateInfo(): User {
        val user = mateChatUiStateFlow.value!!.users.find { it.id == interlocutorUserId }!!

        return user
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

open class MateChatViewModelFactory(
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