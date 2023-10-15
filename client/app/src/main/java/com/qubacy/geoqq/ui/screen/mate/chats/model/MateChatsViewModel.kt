package com.qubacy.geoqq.ui.screen.mate.chats.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.mates.chats.MateChatsOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation.AddChatUiOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class MateChatsViewModel() : WaitingViewModel() {
    // todo: assign to the repository's flow:
    private var mMateChatsOperation: Flow<MateChatsOperation> = flowOf<MateChatsOperation>()

    private var mMateChatsUiOperationFlow: MutableStateFlow<UiOperation?> = MutableStateFlow(null)
    val mateChatsUiOperationFlow: StateFlow<UiOperation?> = mMateChatsUiOperationFlow

    private var mMateChatsUiState = MateChatsUiState()
    val mateChatsUiState: MateChatsUiState get() { return mMateChatsUiState }

    init {
        viewModelScope.launch {
            mMateChatsOperation.collect { onOperationGotten(it) }
        }
    }

    private fun onOperationGotten(operation: MateChatsOperation) {
        // todo: processing the operation..

        processOperation(operation)
    }

    private fun processOperation(operation: MateChatsOperation) {
        // todo: processing an operation according to its type..

//        when (operation::class) {
//
//        }

        // todo: converting GeoChatOperation to GeoChatUiOperation.. (has to be removed soon)

        val mateChatsUiOperation = AddChatUiOperation(
            Chat(0, null, "1st chat",
                Message(0, "hi", 16346363523)))

        viewModelScope.launch {
            mMateChatsUiOperationFlow.emit(mateChatsUiOperation)
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