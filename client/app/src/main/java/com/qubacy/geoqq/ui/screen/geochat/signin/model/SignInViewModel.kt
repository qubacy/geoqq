package com.qubacy.geoqq.ui.screen.geochat.signin.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.model.WaitingViewModel

class SignInViewModel : WaitingViewModel() {
}

class SignInViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignInViewModel::class.java))
            throw IllegalArgumentException()

        return SignInViewModel() as T
    }
}