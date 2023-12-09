package com.qubacy.geoqq.ui.screen.myprofile.model

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.myprofile.model.MyProfileModelContext
import com.qubacy.geoqq.domain.common.model.common.validator.password.standard.StandardPasswordValidator
import com.qubacy.geoqq.domain.common.operation.interrupt.InterruptOperation
import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.operation.SetProfileDataOperation
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.operation.ProfileDataSavedUiOperation
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import kotlinx.coroutines.flow.map

open class MyProfileViewModel(
    private val mMyProfileUseCase: MyProfileUseCase
) : WaitingViewModel() {
    companion object {
        const val TAG = "MyProfileViewModel"
    }

    private var mMyProfileStateFlow = mMyProfileUseCase.stateFlow

    private val mMyProfileUiState = mMyProfileStateFlow.map { stateToUiState(it) }
    val myProfileUiState: LiveData<MyProfileUiState?> = mMyProfileUiState.asLiveData()

    private var mIsGettingMyProfile: Boolean = false
    val isGettingMyProfile get() = mIsGettingMyProfile

    init {
        mMyProfileUseCase.setCoroutineScope(viewModelScope)
    }

    private fun stateToUiState(state: MyProfileState?): MyProfileUiState? {
        if (mIsGettingMyProfile) mIsGettingMyProfile = false
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
            state.hitUpOption,
            uiOperations
        )
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            SetProfileDataOperation::class -> {
                val setProfileDataOperation = operation as SetProfileDataOperation

                mIsWaiting.value = false

                null
            }
            SuccessfulProfileSavingCallbackOperation::class -> {
                val successfulProfileSavingCallbackOperation =
                    operation as SuccessfulProfileSavingCallbackOperation

                mIsWaiting.value = false

                ProfileDataSavedUiOperation()
            }
            InterruptOperation::class -> {
                val interruptOperation = operation as InterruptOperation

                null
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                mIsWaiting.value = false

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
            val currentPassword =
                changedPropHashMap[MyProfileModelContext.CURRENT_PASSWORD_TEXT_KEY].toString()
            val newPassword =
                changedPropHashMap[MyProfileModelContext.NEW_PASSWORD_TEXT_KEY].toString()
            val repeatNewPassword =
                changedPropHashMap[MyProfileModelContext.REPEAT_NEW_PASSWORD_TEXT_KEY].toString()

            return (isPasswordsDataCorrect(currentPassword, newPassword, repeatNewPassword))
        }

        return true
    }

    fun isChangedProfileDataCorrect(
        changedPropHashMap: HashMap<String, Any>
    ): Boolean {
        val changedFields = changedPropHashMap.keys

        if (changedFields.contains(MyProfileModelContext.USER_AVATAR_URI_KEY)) {
            if (!isAvatarDataCorrect(
                    changedPropHashMap[MyProfileModelContext.USER_AVATAR_URI_KEY] as Uri))
            {
                return false
            }
        }
        if (changedFields.contains(MyProfileModelContext.DESCRIPTION_TEXT_KEY)) {
            if (!isDescriptionDataCorrect(
                    changedPropHashMap[MyProfileModelContext.DESCRIPTION_TEXT_KEY] as String))
            {
                return false
            }
        }
        if (!checkPasswordFieldsChange(changedFields, changedPropHashMap)) return false

        if (changedFields.contains(MyProfileModelContext.PRIVACY_HIT_UP_POSITION_KEY)) {
            if (!isPrivacyHitUpOptionCorrect(
                    changedPropHashMap[MyProfileModelContext.PRIVACY_HIT_UP_POSITION_KEY] as Int))
            {
                return false
            }
        }

        return true
    }

    fun getProfileData() {
        mIsWaiting.value = true
        mIsGettingMyProfile = true

        mMyProfileUseCase.getMyProfile()
    }

    fun saveProfileData(
        changedPropHashMap: HashMap<String, Any>
    ) {
        mIsWaiting.value = true

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

                DataMyProfile.HitUpOption.entries.find { it.index == hitUpOptionIndex }
            }

        mMyProfileUseCase.updateMyProfile(
            avatarUri, description, curPassword, newPassword, hitUpOption)
    }

    fun interruptSavingProfileData() {
        mIsWaiting.value = false

        mMyProfileUseCase.interruptOperation()
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

    fun getHitUpOptionByIndex(index: Int): DataMyProfile.HitUpOption? {
        if (index >= DataMyProfile.HitUpOption.entries.size)
            return null

        return DataMyProfile.HitUpOption.entries[index]
    }

    override fun retrieveError(errorId: Long) {
        mMyProfileUseCase.getError(errorId)
    }
}

open class MyProfileViewModelFactory(
    private val mMyProfileUseCase: MyProfileUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel(mMyProfileUseCase) as T
    }
}