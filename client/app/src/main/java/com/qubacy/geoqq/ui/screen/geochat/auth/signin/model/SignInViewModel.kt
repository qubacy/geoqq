package com.qubacy.geoqq.ui.screen.geochat.auth.signin.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.geochat.auth.signin.SignInState
import com.qubacy.geoqq.data.myprofile.entity.myprofile.validator.password.LoginPasswordValidator
import com.qubacy.geoqq.data.common.entity.person.common.validator.username.UsernameValidator
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.AuthViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.state.SignInUiState
import kotlinx.coroutines.launch

// todo: providing a data repository as an argument..
class SignInViewModel : AuthViewModel(), Observer<SignInState> {
    private var mSignInState: LiveData<SignInState>? = null

    private var mSignInUiState: MutableLiveData<SignInUiState> = MutableLiveData<SignInUiState>()
    val signInUiState: LiveData<SignInUiState> = mSignInUiState

    fun isSignInDataCorrect(
        username: String,
        password: String
    ): Boolean {
        if (!isSignInDataFull(username, password))
            return false

        return UsernameValidator().check(username)
            && LoginPasswordValidator().check(password)
    }

    private fun isSignInDataFull(
        username: String,
        password: String
    ): Boolean {
        return (username.isNotEmpty() && password.isNotEmpty())
    }

    fun signIn(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            // todo: conveying the request to the DATA layer..
            // todo: getting signInState LiveData object..


        }

        mIsWaiting.value = true
    }

    fun interruptSignIn() {
        // todo: handling Sign In interruption..

        mSignInState = null
    }

    override fun onChanged(value: SignInState) {
        mIsWaiting.value = false

        // todo: converting SignUpState to SignUpUiState

        mAccessToken.value = "gotten_access_token"
        mSignInUiState.value = SignInUiState()
    }
}

class SignInViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignInViewModel::class.java))
            throw IllegalArgumentException()

        return SignInViewModel() as T
    }
}