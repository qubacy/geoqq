package com.qubacy.geoqq.ui.screen.myprofile.model

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.domain.myprofile.model.MyProfileModelContext
import com.qubacy.geoqq.domain.common.model.validator.password.standard.StandardPasswordValidator
import com.qubacy.geoqq.domain.common.operation.InterruptOperation
import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.operation.ProfileDataSavedUiOperation
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MyProfileViewModel(
    val myProfileUseCase: MyProfileUseCase
) : WaitingViewModel() {
    private var mMyProfileStateFlow = myProfileUseCase.stateFlow

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
            null,
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
            InterruptOperation::class -> {
                val interruptOperation = operation as InterruptOperation

                null
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

        if (changedFields.contains(MyProfileModelContext.CURRENT_PASSWORD_TEXT_KEY))
            ++passwordChangingFieldCount
        if (changedFields.contains(MyProfileModelContext.NEW_PASSWORD_TEXT_KEY))
            ++passwordChangingFieldCount
        if (changedFields.contains(MyProfileModelContext.REPEAT_NEW_PASSWORD_TEXT_KEY))
            ++passwordChangingFieldCount

        if (passwordChangingFieldCount != 0 && passwordChangingFieldCount < 3) {
            return false

        } else if (passwordChangingFieldCount == 3) {
            val currentPassword = changedPropHashMap[MyProfileModelContext.CURRENT_PASSWORD_TEXT_KEY].toString()
            val newPassword = changedPropHashMap[MyProfileModelContext.NEW_PASSWORD_TEXT_KEY].toString()
            val repeatNewPassword = changedPropHashMap[MyProfileModelContext.REPEAT_NEW_PASSWORD_TEXT_KEY].toString()

            return (isPasswordsDataCorrect(currentPassword, newPassword, repeatNewPassword))
        }

        return true
    }

    fun isChangedProfileDataCorrect(
        changedPropHashMap: HashMap<String, Any>
    ): Boolean {
        val changedFields = changedPropHashMap.keys

        if (changedFields.contains(MyProfileModelContext.USER_AVATAR_URI_KEY)) {
            if (!isAvatarDataCorrect(changedPropHashMap[MyProfileModelContext.USER_AVATAR_URI_KEY] as Uri))
                return false
        }
        if (changedFields.contains(MyProfileModelContext.DESCRIPTION_TEXT_KEY)) {
            if (!isDescriptionDataCorrect(changedPropHashMap[MyProfileModelContext.DESCRIPTION_TEXT_KEY] as String)){
                return false
            }
        }
        if (!checkPasswordFieldsChange(changedFields, changedPropHashMap)) return false

        if (changedFields.contains(MyProfileModelContext.PRIVACY_HIT_UP_POSITION_KEY)) {
            if (!isPrivacyHitUpOptionCorrect(changedPropHashMap[MyProfileModelContext.PRIVACY_HIT_UP_POSITION_KEY] as Int))
                return false
        }

        return true
    }

    fun getProfileData() {
        mIsWaiting.value = true

        viewModelScope.launch(Dispatchers.IO) {
            myProfileUseCase.getMyProfile()
        }
    }

    fun saveProfileData(
        changedPropHashMap: HashMap<String, Any>
    ) {
        mIsWaiting.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val avatarUri = changedPropHashMap[MyProfileModelContext.USER_AVATAR_URI_KEY]
                ?.toString()?.toUri()
            val description = changedPropHashMap[MyProfileModelContext.DESCRIPTION_TEXT_KEY]
                ?.toString()
            val curPassword = changedPropHashMap[MyProfileModelContext.CURRENT_PASSWORD_TEXT_KEY]
                ?.toString()
            val newPassword = changedPropHashMap[MyProfileModelContext.NEW_PASSWORD_TEXT_KEY]
                ?.toString()
            val hitUpOption =
                if (changedPropHashMap[MyProfileModelContext.PRIVACY_HIT_UP_POSITION_KEY] == null) {
                    null
                } else {
                    val hitUpOptionIndex =
                        (changedPropHashMap[MyProfileModelContext.PRIVACY_HIT_UP_POSITION_KEY] as Int)

                    MyProfileDataModelContext.HitUpOption.entries.find { it.index == hitUpOptionIndex }
                }

            myProfileUseCase.updateMyProfile(
                avatarUri, description, curPassword, newPassword, hitUpOption)
        }
    }

    fun interruptSavingProfileData() {
        mIsWaiting.value = false

        myProfileUseCase.interruptOperation()
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

        val loginPasswordValidator = StandardPasswordValidator()

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

    fun getHitUpOptionByIndex(index: Int): MyProfileDataModelContext.HitUpOption? {
        if (index >= MyProfileDataModelContext.HitUpOption.entries.size)
            return null

        return MyProfileDataModelContext.HitUpOption.entries[index]
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

class MyProfileViewModelFactory(
    val myProfileUseCase: MyProfileUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel(myProfileUseCase) as T
    }
}