package com.qubacy.geoqq.ui.screen.geochat.chat.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.chat.geo.GeoChatOperation
import com.qubacy.geoqq.data.common.entity.message.validator.MessageTextValidator
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.common.chat.model.state.operation.ChatUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.state.operation.SetMessagesUiOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class GeoChatViewModel : LocationViewModel() {
    // todo: assign to the repository's flow:
    private var mGeoChatOperation: Flow<GeoChatOperation> = flowOf<GeoChatOperation>()

    private var mGeoChatUiOperation: MutableLiveData<ChatUiOperation> = MutableLiveData()
    val geoChatUiOperation: LiveData<ChatUiOperation> = mGeoChatUiOperation

    private var mGeoChatUiState = MutableLiveData(ChatUiState())
    val geoChatUiState: LiveData<ChatUiState> = mGeoChatUiState

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

    fun onOperationGotten(operation: GeoChatOperation) {
        // todo: converting GeoChatOperation to GeoChatUiOperation..

        val geoChatUiOperation = SetMessagesUiOperation(listOf())

        mGeoChatUiOperation.value = geoChatUiOperation

        // todo: processing an operation to make UI state evolve..

        processOperation(operation)
    }

    private fun processOperation(operation: GeoChatOperation) {
        // todo: processing an operation according to its type..

//        when (operation::class) {
//
//        }

//        mGeoChatUiState = GeoChatUiState()
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