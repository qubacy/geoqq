package com.qubacy.geoqq.ui.screen.geochat.auth.signin.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.myprofile.entity.myprofile.validator.password.LoginPasswordValidator
import com.qubacy.geoqq.data.common.entity.person.common.validator.username.UsernameValidator
import com.qubacy.geoqq.data.common.auth.operation.AuthorizeOperation
import com.qubacy.geoqq.data.common.auth.state.AuthState
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.AuthViewModel
import kotlinx.coroutines.launch

// todo: providing a data repository as an argument..
class SignInViewModel(

) : AuthViewModel() {
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

            mAuthStateFlow.emit(AuthState(true, listOf(AuthorizeOperation())))
        }

        //mIsWaiting.value = true
    }

    fun interruptSignIn() {
        // todo: handling Sign In interruption..

        mIsWaiting.value = false
    }
}

class SignInViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignInViewModel::class.java))
            throw IllegalArgumentException()

        return SignInViewModel() as T
    }
}