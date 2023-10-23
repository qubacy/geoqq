package com.qubacy.geoqq.ui.screen.myprofile.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.entity.person.common.validator.username.UsernameValidator
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.myprofile.MyProfileContext
import com.qubacy.geoqq.data.myprofile.entity.myprofile.validator.password.LoginPasswordValidator
import com.qubacy.geoqq.data.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.data.myprofile.state.MyProfileState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.operation.ProfileDataSavedUiOperation
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class MyProfileViewModel(

) : WaitingViewModel() {
    // todo: assign to the repository's flow:
    private var mMyProfileStateFlow = MutableStateFlow<MyProfileState?>(null)

    private val mMyProfileUiState = mMyProfileStateFlow.map { stateToUiState(it) }
    val myProfileUiState: LiveData<MyProfileUiState?> = mMyProfileUiState.asLiveData()

    private fun stateToUiState(state: MyProfileState?): MyProfileUiState? {
        if (isWaiting.value == true) isWaiting.value = false
        if (state == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in state.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return MyProfileUiState(
            state.avatar,
            state.username,
            state.description,
            state.password,
            state.hitUpOption,
            uiOperations
        )
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            SuccessfulProfileSavingCallbackOperation::class -> {
                val successfulProfileSavingCallbackOperation =
                    operation as SuccessfulProfileSavingCallbackOperation

                ProfileDataSavedUiOperation()
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun saveProfileData(
        username: String,
        description: String,
        password: String,
        passwordConfirmation: String,
        hitUpOption: MyProfileContext.HitUpOption
    ) {
        viewModelScope.launch {
            // todo: sending data to the DATA layer..

        }

        mIsWaiting.value = true
    }

    fun interruptSavingProfileData() {
        // todo: handling an interruption process..


    }

    fun isProfileDataCorrect(
        username: String,
        description: String,
        password: String,
        passwordConfirmation: String,
        hitUpOption: MyProfileContext.HitUpOption
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

    fun getHitUpOptionByIndex(index: Int): MyProfileContext.HitUpOption {
        return MyProfileContext.HitUpOption.entries[index]
    }
}

class MyProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel() as T
    }
}