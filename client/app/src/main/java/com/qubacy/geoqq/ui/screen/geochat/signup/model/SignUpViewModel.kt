package com.qubacy.geoqq.ui.screen.geochat.signup.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.model.WaitingViewModel

class SignUpViewModel : WaitingViewModel() {

}

class SignUpViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignUpViewModel::class.java))
            throw IllegalArgumentException()

        return SignUpViewModel() as T
    }
}