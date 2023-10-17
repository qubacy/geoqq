package com.qubacy.geoqq.ui.screen.geochat.chat.model

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.geochat.chat.GeoChatOperation
import com.qubacy.geoqq.data.common.entity.chat.message.validator.MessageTextValidator
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class GeoChatViewModel : LocationViewModel() {
    // todo: assign to the repository's flow:
    private var mGeoChatOperation: Flow<GeoChatOperation> = flowOf<GeoChatOperation>()

    private var mGeoChatUiOperationFlow: MutableStateFlow<UiOperation?> = MutableStateFlow(null)
    val geoChatUiOperationFlow: StateFlow<UiOperation?> = mGeoChatUiOperationFlow

    private var mGeoChatUiState = ChatUiState(listOf(), listOf(), listOf())
    val geoChatUiState: ChatUiState get() { return mGeoChatUiState }

    init {
        viewModelScope.launch {
            mGeoChatOperation.collect { onOperationGotten(it) }
        }
    }

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

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // todo: sending a message using DATA layer..
        }
    }

    private fun onOperationGotten(operation: GeoChatOperation) {
        // todo: processing an operation to make UI state evolve..

        processOperation(operation)
    }

    private fun processOperation(operation: GeoChatOperation) {
        // todo: processing an operation according to its type..

//        when (operation::class) {
//
//        }
    }

    fun addToFriend(user: User) {
        viewModelScope.launch {
            // todo: conveying a request to the DATA layer..


        }
    }
}

class GeoChatViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatViewModel() as T
    }
}