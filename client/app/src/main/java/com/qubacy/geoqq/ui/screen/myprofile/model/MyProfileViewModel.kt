package com.qubacy.geoqq.ui.screen.myprofile.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.myprofile.MyProfileContext
import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext
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

    // todo: delete:
    init {
        mMyProfileStateFlow.tryEmit(
            MyProfileState(
                null,
                "fqwfqwf",
                "fqffqwf fqwf qwfqwfqwf0",
                hitUpOption = MyProfileContext.HitUpOption.NEGATIVE
            )
        )
    }

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

    private fun checkPasswordFieldsChange(
        changedFields: Set<String>,
        changedPropHashMap: HashMap<String, Any>
    ): Boolean {
        var passwordChangingFieldCount = 0

        if (changedFields.contains(MyProfileEntityContext.CURRENT_PASSWORD_TEXT_KEY))
            ++passwordChangingFieldCount
        if (changedFields.contains(MyProfileEntityContext.NEW_PASSWORD_TEXT_KEY))
            ++passwordChangingFieldCount
        if (changedFields.contains(MyProfileEntityContext.REPEAT_NEW_PASSWORD_TEXT_KEY))
            ++passwordChangingFieldCount

        if (passwordChangingFieldCount != 0 && passwordChangingFieldCount < 3) {
            return false

        } else if (passwordChangingFieldCount == 3) {
            val currentPassword = changedPropHashMap[MyProfileEntityContext.CURRENT_PASSWORD_TEXT_KEY].toString()
            val newPassword = changedPropHashMap[MyProfileEntityContext.NEW_PASSWORD_TEXT_KEY].toString()
            val repeatNewPassword = changedPropHashMap[MyProfileEntityContext.REPEAT_NEW_PASSWORD_TEXT_KEY].toString()

            return (isPasswordsDataCorrect(currentPassword, newPassword, repeatNewPassword))
        }

        return true
    }

    fun isChangedProfileDataCorrect(
        changedPropHashMap: HashMap<String, Any>
    ): Boolean {
        val changedFields = changedPropHashMap.keys

        if (changedFields.contains(MyProfileEntityContext.USER_AVATAR_URI_KEY)) {
            if (!isAvatarDataCorrect(changedPropHashMap[MyProfileEntityContext.USER_AVATAR_URI_KEY] as Uri))
                return false
        }
        if (changedFields.contains(MyProfileEntityContext.DESCRIPTION_TEXT_KEY)) {
            if (!isDescriptionDataCorrect(changedPropHashMap[MyProfileEntityContext.DESCRIPTION_TEXT_KEY] as String)){
                return false
            }
        }
        if (!checkPasswordFieldsChange(changedFields, changedPropHashMap)) return false

        if (changedFields.contains(MyProfileEntityContext.PRIVACY_HIT_UP_POSITION_KEY)) {
            if (!isPrivacyHitUpOptionCorrect(changedPropHashMap[MyProfileEntityContext.PRIVACY_HIT_UP_POSITION_KEY] as Int))
                return false
        }

        return true
    }

    fun saveProfileData(
        changedPropHashMap: HashMap<String, Any>
    ) {
        viewModelScope.launch {
            // todo: sending data to the DATA layer..

        }

        mIsWaiting.value = true
    }

    fun interruptSavingProfileData() {
        // todo: handling an interruption process..


    }

    private fun isAvatarDataCorrect(
        avatarUri: Uri
    ): Boolean {

        return true // todo: how to validate?
    }

    private fun isDescriptionDataCorrect(
        description: String
    ): Boolean {
        if (description.isEmpty()) return false

        return true
    }

    private fun isPasswordsDataCorrect(
        currentPassword: String,
        newPassword: String,
        repeatNewPassword: String,
    ): Boolean {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || repeatNewPassword.isEmpty()
        || (newPassword != repeatNewPassword)
        ) {
            return false
        }

        val loginPasswordValidator = LoginPasswordValidator()

        return (loginPasswordValidator.check(currentPassword)
                && loginPasswordValidator.check(newPassword)
                && loginPasswordValidator.check(repeatNewPassword))
    }

    private fun isPrivacyHitUpOptionCorrect(
        hitUpOptionIndex: Int
    ): Boolean {
        if (getHitUpOptionByIndex(hitUpOptionIndex) == null) return false

        return true
    }

    fun getHitUpOptionByIndex(index: Int): MyProfileContext.HitUpOption? {
        if (index >= MyProfileContext.HitUpOption.entries.size)
            return null

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