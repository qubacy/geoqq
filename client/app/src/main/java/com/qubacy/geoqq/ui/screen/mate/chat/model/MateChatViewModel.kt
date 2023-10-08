package com.qubacy.geoqq.ui.screen.mate.chat.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.model.BaseViewModel

class MateChatViewModel() : BaseViewModel() {

}

class MateChatViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateChatViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatViewModel() as T
    }
}