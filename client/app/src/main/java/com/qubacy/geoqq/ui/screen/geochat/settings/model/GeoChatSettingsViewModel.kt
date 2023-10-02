package com.qubacy.geoqq.ui.screen.geochat.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.model.WaitingViewModel

class GeoChatSettingsViewModel : WaitingViewModel() {
}

class GeoChatSettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatSettingsViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatSettingsViewModel() as T
    }
}