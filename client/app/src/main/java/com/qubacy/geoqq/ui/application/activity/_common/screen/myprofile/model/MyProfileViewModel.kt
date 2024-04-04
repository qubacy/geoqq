package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.logout.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.MyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.toMyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.DeleteMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.UpdateMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.MyProfileInputData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.toMyProfileUpdateData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.toUpdatedMyProfilePresentation
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
    }

    override fun generateDefaultUiState(): MyProfileUiState {
        return MyProfileUiState()
    }

    fun preserveInputData(inputData: MyProfileInputData) {
        mUiState.myProfileInputData = inputData
    }

    fun isUpdateDataValid(updateData: MyProfileInputData): Boolean {
        if (updateData.isEmpty()) return false

        if (updateData.password != null) {
            if (updateData.newPassword != updateData.newPasswordAgain) return false
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
            LogoutDomainResult::class ->
                processLogoutDomainResult(domainResult as LogoutDomainResult)
            else -> null
        }
    }

    private fun processGetMyProfileDomainResult(
        getMyProfileResult: GetMyProfileDomainResult
    ): UiOperation {
        changeLoadingState(false)

        val myProfilePresentation = getMyProfileResult.myProfile?.toMyProfilePresentation()

        if (!getMyProfileResult.isSuccessful())
            return ErrorUiOperation(getMyProfileResult.error!!)

        mUiState.myProfilePresentation = myProfilePresentation!!

        return GetMyProfileUiOperation(myProfilePresentation)
    }

    private fun processUpdateMyProfileDomainResult(
        updateMyProfileDomainResult: UpdateMyProfileDomainResult
    ): UiOperation {
        changeLoadingState(false)

        if (!updateMyProfileDomainResult.isSuccessful())
            return ErrorUiOperation(updateMyProfileDomainResult.error!!)

        mUiState.myProfilePresentation = getUpdatedMyProfilePresentation()

        return UpdateMyProfileUiOperation()
    }

    private fun getUpdatedMyProfilePresentation(): MyProfilePresentation {
        val prevMyProfilePresentation = mUiState.myProfilePresentation
            ?: throw IllegalStateException()

        return mUiState.myProfileInputData
            .toUpdatedMyProfilePresentation(prevMyProfilePresentation)
    }

    private fun processDeleteMyProfileDomainResult(
        deleteMyProfileDomainResult: DeleteMyProfileDomainResult
    ): UiOperation {
        changeLoadingState(false)

        if (!deleteMyProfileDomainResult.isSuccessful())
            return ErrorUiOperation(deleteMyProfileDomainResult.error!!)

        return DeleteMyProfileUiOperation()
    }

    private fun processLogoutDomainResult(logoutDomainResult: LogoutDomainResult): UiOperation {
        changeLoadingState(false)

        if (!logoutDomainResult.isSuccessful())
            return ErrorUiOperation(logoutDomainResult.error!!)

        return LogoutUiOperation()
    }

    fun getMyProfile() {
        changeLoadingState(true)
        mUseCase.getMyProfile()
    }

    fun updateMyProfile(updateData: MyProfileInputData) {
        changeLoadingState(true)

        mUiState.myProfileInputData = updateData

        mUseCase.updateMyProfile(updateData.toMyProfileUpdateData())
    }

    fun deleteMyProfile() {
        changeLoadingState(true)
        mUseCase.deleteMyProfile()
    }

    fun logout() {
        changeLoadingState(true)
        mUseCase.logout()
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