package com.qubacy.geoqq.ui.screen.mate.request.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel

class MateRequestsViewModel() : WaitingViewModel() {

}

class MateRequestsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateRequestsViewModel::class.java))
            throw IllegalArgumentException()

        return MateRequestsViewModel() as T
    }
}