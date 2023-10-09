package com.qubacy.geoqq.ui.screen.geochat.chat.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.chat.geo.GeoChatState
import com.qubacy.geoqq.data.common.entity.message.validator.MessageTextValidator
import com.qubacy.geoqq.ui.common.fragment.location.model.LocationViewModel
import kotlinx.coroutines.launch

class GeoChatViewModel : LocationViewModel(), Observer<GeoChatState> {
    private var mGeoChatState: LiveData<GeoChatState>? = null

    private var mGeoChatUiState: MutableLiveData<GeoChatUiState> = MutableLiveData()
    val geoChatUiState: LiveData<GeoChatUiState> = mGeoChatUiState

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
            // todo: getting a new instance of GeoChatState..
        }
    }

    override fun onChanged(value: GeoChatState) {
        // todo: converting GeoChatState to GeoChatUiState

        mGeoChatUiState.value = GeoChatUiState(listOf(), listOf())
    }
}

class GeoChatViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatViewModel() as T
    }
}