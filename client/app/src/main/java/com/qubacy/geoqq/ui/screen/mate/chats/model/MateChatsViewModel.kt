package com.qubacy.geoqq.ui.screen.mate.chats.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.operation.AddChatOperation
import com.qubacy.geoqq.domain.mate.chats.operation.AddPrecedingChatsOperation
import com.qubacy.geoqq.domain.mate.chats.operation.SetMateChatsOperation
import com.qubacy.geoqq.domain.mate.chats.operation.UpdateChatOperation
import com.qubacy.geoqq.domain.mate.chats.operation.UpdateRequestCountOperation
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.AddChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.AddPrecedingChatsUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.SetMateChatsUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateRequestCountUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateUsersUiOperation
import kotlinx.coroutines.flow.map

open class MateChatsViewModel(
    private val mMateChatsUseCase: MateChatsUseCase
) : WaitingViewModel() {
    companion object {
        const val DEFAULT_CHAT_CHUNK_SIZE = 20
    }

    private var mMateChatsStateFlow = mMateChatsUseCase.stateFlow

    private val mMateChatsUiStateFlow = mMateChatsStateFlow.map { chatsStateToUiState(it) }
    val mateChatsUiStateFlow: LiveData<MateChatsUiState?> = mMateChatsUiStateFlow.asLiveData()

    private var mIsGettingChats: Boolean = false
    val isGettingChats get() = mIsGettingChats

    init {
        mMateChatsUseCase.setCoroutineScope(viewModelScope)
    }

    private fun chatsStateToUiState(mateChatsState: MateChatsState?): MateChatsUiState? {
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

                mIsWaiting.value = false
                mIsGettingChats = false

                SetMateChatsUiOperation()
            }
            SetUsersDetailsOperation::class -> {
                val setUsersDetailsOperation = operation as SetUsersDetailsOperation

                UpdateUsersUiOperation(setUsersDetailsOperation.usersIds)
            }
            AddPrecedingChatsOperation::class -> {
                val addPrecedingChatsOperation = operation as AddPrecedingChatsOperation

                mIsGettingChats = false

                AddPrecedingChatsUiOperation(
                    addPrecedingChatsOperation.precedingChats,
                    addPrecedingChatsOperation.areUpdated
                )
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

                mIsWaiting.value = false

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun getMateChats() {
        mIsWaiting.value = true
        mIsGettingChats = true

        mMateChatsUseCase.getMateChats(DEFAULT_CHAT_CHUNK_SIZE)
    }

    fun chatListEndReached() {
        val curState = mateChatsUiStateFlow.value

        if (curState == null || mIsGettingChats) return

        mIsGettingChats = true

        val curChatCount = curState.chats.size
        val nextChatCount = curChatCount + DEFAULT_CHAT_CHUNK_SIZE

        if (nextChatCount % DEFAULT_CHAT_CHUNK_SIZE != 0) return

        mMateChatsUseCase.getMateChats(nextChatCount)
    }

    override fun retrieveError(errorId: Long) {
        mMateChatsUseCase.getError(errorId)
    }
}

open class MateChatsViewModelFactory(
    private val mateChatsUseCase: MateChatsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModel(mateChatsUseCase) as T
    }
}