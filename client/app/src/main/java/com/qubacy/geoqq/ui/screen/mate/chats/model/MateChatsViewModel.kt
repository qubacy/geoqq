package com.qubacy.geoqq.ui.screen.mate.chats.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel

class MateChatsViewModel() : WaitingViewModel() {

}

class MateChatsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModel() as T
    }
}