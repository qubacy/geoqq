package com.qubacy.geoqq.ui.screen.geochat.auth.signup.model

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
class SignUpViewModel(

) : AuthViewModel() {
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

            mAuthStateFlow.emit(AuthState(true, listOf(AuthorizeOperation())))
        }

//        mIsWaiting.value = true
    }

    fun interruptSignUp() {
        // todo: handling Sign Up interruption..

        mIsWaiting.value = false
    }
}

class SignUpViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(SignUpViewModel::class.java))
            throw IllegalArgumentException()

        return SignUpViewModel() as T
    }
}