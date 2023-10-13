package com.qubacy.geoqq.ui.screen.myprofile.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.entity.person.common.validator.username.UsernameValidator
import com.qubacy.geoqq.data.common.entity.person.myprofile.validator.password.LoginPasswordValidator
import com.qubacy.geoqq.data.myprofile.MyProfileOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.BaseViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class MyProfileViewModel : BaseViewModel() {
    enum class HitUpOption(
        val index: Int
    ) {
        POSITIVE(0), NEGATIVE(1);
    }

    private var mMyProfileOperationFlow = flowOf(MyProfileOperation())

    // todo: assign to the repository's live data:
    private val mMyProfileUiState: MutableLiveData<MyProfileUiState> = MutableLiveData()
    val myProfileUiState: LiveData<MyProfileUiState> = mMyProfileUiState

    init {
        viewModelScope.launch {
            mMyProfileOperationFlow.collect {
                onOperationGotten(it)
            }
        }
    }

    fun saveProfileData(
        username: String,
        description: String,
        password: String,
        passwordConfirmation: String,
        hitUpOption: HitUpOption
    ) {
        viewModelScope.launch {
            // todo: sending data to the DATA layer..

        }
    }

    fun isProfileDataCorrect(
        username: String,
        description: String,
        password: String,
        passwordConfirmation: String,
        hitUpOption: HitUpOption
    ): Boolean {
        if (!isProfileDataFull(username, description, password, passwordConfirmation)
         || password != passwordConfirmation)
        {
            return false
        }

        return (UsernameValidator().check(username) && LoginPasswordValidator().check(password)
             && LoginPasswordValidator().check(password))
    }

    private fun isProfileDataFull(
        username: String,
        description: String,
        password: String,
        passwordConfirmation: String
    ): Boolean {
        return (username.isNotEmpty() && description.isNotEmpty()
             && password.isNotEmpty() && passwordConfirmation.isNotEmpty())
    }

    fun getHitUpOptionByIndex(index: Int): HitUpOption {
        return HitUpOption.entries[index]
    }

    private fun onOperationGotten(operation: MyProfileOperation) {
        // todo: processing the operation..


    }
}

class MyProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel() as T
    }
}