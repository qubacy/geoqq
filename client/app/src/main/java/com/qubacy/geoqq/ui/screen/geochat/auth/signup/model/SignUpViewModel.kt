package com.qubacy.geoqq.ui.screen.geochat.auth.signup.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.entity.person.myprofile.validator.password.LoginPasswordValidator
import com.qubacy.geoqq.data.common.entity.person.common.validator.username.UsernameValidator
import com.qubacy.geoqq.data.auth.signup.SignUpState
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.AuthViewModel
import kotlinx.coroutines.launch

// todo: providing a data repository as an argument..
class SignUpViewModel() : AuthViewModel(), Observer<SignUpState> {
    private var mSignUpState: LiveData<SignUpState>? = null

    private var mSignUpUiState: MutableLiveData<SignUpUiState> = MutableLiveData<SignUpUiState>()
    val signUpUiState: LiveData<SignUpUiState> = mSignUpUiState

    fun isSignUpDataCorrect(
        username: String,
        password: String,
        confirmationPassword: String
    ): Boolean {
        if (!(isSignUpDataFull(username, password, confirmationPassword)
         || password != confirmationPassword))
        {
            return false
        }

        return UsernameValidator().check(username)
            && LoginPasswordValidator().check(password)
            && LoginPasswordValidator().check(confirmationPassword)
    }

    private fun isSignUpDataFull(
        username: String,
        password: String,
        confirmationPassword: String
    ): Boolean {
        return (username.isNotEmpty() && password.isNotEmpty() && confirmationPassword.isNotEmpty())
    }

    fun signUp(
        username: String,
        password: String,
        confirmationPassword: String
    ) {
        viewModelScope.launch {
            // todo: conveying the data to the DATA layer..
            // todo: getting signUpState LiveData object..


        }

        mIsWaiting.value = true
    }

    fun interruptSignUp() {
        // todo: handling Sign Up interruption..

        mSignUpState = null
    }

    override fun onChanged(value: SignUpState) {
        mIsWaiting.value = false

        // todo: converting SignUpState to SignUpUiState

        mAccessToken.value = "gotten_access_token"
        mSignUpUiState.value = SignUpUiState()
    }
}

class SignUpViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignUpViewModel::class.java))
            throw IllegalArgumentException()

        return SignUpViewModel() as T
    }
}