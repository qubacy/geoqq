package com.qubacy.geoqq.ui.screen.mate.chats.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.mates.chats.entity.MateChatPreview
import com.qubacy.geoqq.data.mates.chats.operation.AddChatOperation
import com.qubacy.geoqq.data.mates.chats.operation.UpdateChatOperation
import com.qubacy.geoqq.data.mates.chats.operation.UpdateRequestCountOperation
import com.qubacy.geoqq.data.mates.chats.state.MateChatsState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.AddChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateRequestCountUiOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

// todo: provide a repository as a param..
class MateChatsViewModel() : WaitingViewModel() {
    // todo: assign to the repository's flow:
    private var mMateChatsStateFlow = MutableStateFlow<MateChatsState?>(null)

    private val mMateChatsUiStateFlow = mMateChatsStateFlow.map { chatsStateToUiState(it) }
    val mateChatsUiStateFlow: LiveData<MateChatsUiState?> = mMateChatsUiStateFlow.asLiveData()

    // todo: delete:
    init {
        mMateChatsStateFlow.tryEmit(MateChatsState(
            listOf(MateChatPreview(0, null, "somebody", Message(0, 0, "test", 124125125125))),
            listOf(User(0, "me")),
            3
        ))
    }

    private fun chatsStateToUiState(mateChatsState: MateChatsState?): MateChatsUiState? {
        if (mateChatsState == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in mateChatsState.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return MateChatsUiState(
            mateChatsState.chatPreviews,
            mateChatsState.users,
            mateChatsState.requestCount,
            uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            // todo: should it contain a new user adding case????

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

}

class MateChatsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModel() as T
    }
}