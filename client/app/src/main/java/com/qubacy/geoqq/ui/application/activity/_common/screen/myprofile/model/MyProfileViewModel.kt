package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common.model.password.PasswordContext
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.toMyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.DeleteMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.UpdateMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.MyProfileInputData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.toMyProfileUpdateData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MyProfileViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mMyProfileUseCase: MyProfileUseCase
) : BusinessViewModel<MyProfileUiState, MyProfileUseCase>(
    mSavedStateHandle, mErrorDataRepository, mMyProfileUseCase
) {
    companion object {
        const val TAG = "MyProfileViewModel"

        val ABOUT_ME_REGEX = Regex("^\\S+\$")
        val PASSWORD_REGEX = PasswordContext.REGEX
    }

    override fun generateDefaultUiState(): MyProfileUiState {
        return MyProfileUiState()
    }

    fun preserveInputData(inputData: MyProfileInputData) {
        mUiState.myProfileInputData = inputData
    }

    fun isUpdateDataValid(updateData: MyProfileInputData): Boolean {
        if (updateData.isEmpty()) return false

        if (updateData.aboutMe != null)
            if (!ABOUT_ME_REGEX.matches(updateData.aboutMe)) return false

        if (updateData.password != null) {
            if (!PASSWORD_REGEX.matches(updateData.password)) return false

            if (updateData.newPassword.isNullOrEmpty()
             || updateData.newPasswordAgain.isNullOrEmpty()
            ) {
                return false
            }

            if (updateData.newPassword != updateData.newPasswordAgain) return false
            if (!PASSWORD_REGEX.matches(updateData.newPassword)) return false
        }

        return true
    }

    override fun processDomainResultFlow(domainResult: DomainResult): UiOperation? {
        val uiOperation = super.processDomainResultFlow(domainResult)

        if (uiOperation != null) return uiOperation

        return when (domainResult::class) {
            GetMyProfileDomainResult::class ->
                processGetMyProfileDomainResult(domainResult as GetMyProfileDomainResult)
            UpdateMyProfileDomainResult::class ->
                processUpdateMyProfileDomainResult(domainResult as UpdateMyProfileDomainResult)
            DeleteMyProfileDomainResult::class ->
                processDeleteMyProfileDomainResult(domainResult as DeleteMyProfileDomainResult)
            else -> null
        }
    }

    private fun processGetMyProfileDomainResult(
        getMyProfileResult: GetMyProfileDomainResult
    ): UiOperation {
        changeLoadingState(false)

        val uiOperation =
            if (!getMyProfileResult.isSuccessful()) ErrorUiOperation(getMyProfileResult.error!!)
            else GetMyProfileUiOperation(getMyProfileResult.myProfile!!.toMyProfilePresentation())

        // todo: is it necessary to update mUiState here?

        return uiOperation
    }

    private fun processUpdateMyProfileDomainResult(
        updateMyProfileDomainResult: UpdateMyProfileDomainResult
    ): UiOperation {
        changeLoadingState(false)

        val uiOperation =
            if (!updateMyProfileDomainResult.isSuccessful())
                ErrorUiOperation(updateMyProfileDomainResult.error!!)
            else UpdateMyProfileUiOperation()

        return uiOperation
    }

    private fun processDeleteMyProfileDomainResult(
        deleteMyProfileDomainResult: DeleteMyProfileDomainResult
    ): UiOperation {
        changeLoadingState(false)

        val uiOperation =
            if (!deleteMyProfileDomainResult.isSuccessful())
                ErrorUiOperation(deleteMyProfileDomainResult.error!!)
            else DeleteMyProfileUiOperation()

        return uiOperation
    }

    fun getMyProfile() {
        mUseCase.getMyProfile()
    }

    fun updateMyProfile(updateData: MyProfileInputData) {
        mUseCase.updateMyProfile(updateData.toMyProfileUpdateData())
    }

    fun deleteMyProfile() {
        mUseCase.deleteMyProfile()
    }

    fun logout() {
        // todo: implement..


    }
}

@Qualifier
annotation class MyProfileViewModelFactoryQualifier

class MyProfileViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mMyProfileUseCase: MyProfileUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel(handle, mErrorDataRepository, mMyProfileUseCase) as T
    }
}