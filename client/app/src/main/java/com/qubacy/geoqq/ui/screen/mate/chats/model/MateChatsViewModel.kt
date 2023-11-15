package com.qubacy.geoqq.ui.screen.mate.chats.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.domain.common.operation.chat.SetUserDetailsOperation
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.operation.AddChatOperation
import com.qubacy.geoqq.domain.mate.chats.operation.SetMateChatsOperation
import com.qubacy.geoqq.domain.mate.chats.operation.UpdateChatOperation
import com.qubacy.geoqq.domain.mate.chats.operation.UpdateRequestCountOperation
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.AddChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.SetMateChatsUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateRequestCountUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateUserUiOperation
import kotlinx.coroutines.flow.map

class MateChatsViewModel(
    val mateChatsUseCase: MateChatsUseCase
) : WaitingViewModel() {
    companion object {
        const val DEFAULT_CHAT_CHUNK_SIZE = 20
    }

    private var mMateChatsStateFlow = mateChatsUseCase.stateFlow

    private val mMateChatsUiStateFlow = mMateChatsStateFlow.map { chatsStateToUiState(it) }
    val mateChatsUiStateFlow: LiveData<MateChatsUiState?> = mMateChatsUiStateFlow.asLiveData()

    init {
        mateChatsUseCase.setCoroutineScope(viewModelScope)
    }

    private fun chatsStateToUiState(mateChatsState: MateChatsState?): MateChatsUiState? {
        if (mIsWaiting.value == true) mIsWaiting.value = false
        if (mateChatsState == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in mateChatsState.newOperations) {
            val uiOperation = processOperation(operation, mateChatsState)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return MateChatsUiState(
            mateChatsState.chats,
            mateChatsState.users,
            mateChatsState.mateRequestCount,
            uiOperations)
    }

    private fun processOperation(operation: Operation, state: MateChatsState): UiOperation? {
        return when (operation::class) {
            SetMateChatsOperation::class -> {
                val setMateChatsOperation = operation as SetMateChatsOperation

                SetMateChatsUiOperation()
            }
            SetUserDetailsOperation::class -> {
                val setUserDetailsOperation = operation as SetUserDetailsOperation

                UpdateUserUiOperation(setUserDetailsOperation.userId)
            }
            AddChatOperation::class -> {
                val addChatOperation = operation as AddChatOperation

                // mb processing the operation..

                AddChatUiOperation(addChatOperation.chatId)
            }
            UpdateChatOperation::class -> {
                val updateChatOperation = operation as UpdateChatOperation

                // mb processing the operation..

                UpdateChatUiOperation(updateChatOperation.chatId)
            }
            UpdateRequestCountOperation::class -> {
                val updateRequestCountOperation = operation as UpdateRequestCountOperation

                // ??

                UpdateRequestCountUiOperation()
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun getMateChats() {
        mIsWaiting.value = true

        mateChatsUseCase.getMateChats(DEFAULT_CHAT_CHUNK_SIZE)
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

class MateChatsViewModelFactory(
    private val mateChatsUseCase: MateChatsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModel(mateChatsUseCase) as T
    }
}