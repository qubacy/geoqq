package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.domain.myprofile.usecase.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.profile.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.logout.usecase.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.profile.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.MyProfileUpdatedDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.MyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.toMyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.MyProfileDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.profile.get.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.MyProfileUpdatedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.profile.update.UpdateMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.result.handler.MyProfileDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.MyProfileInputData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.toMyProfileUpdateData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.toUpdatedMyProfilePresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MyProfileViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDataSource,
    mMyProfileUseCase: MyProfileUseCase
) : BusinessViewModel<MyProfileUiState, MyProfileUseCase>(
    mSavedStateHandle, mErrorSource, mMyProfileUseCase
), AuthorizedViewModel {
    companion object {
        const val TAG = "MyProfileViewModel"
    }

    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(AuthorizedDomainResultHandler(this))
            .plus(MyProfileDomainResultHandler(this))
    }

    override fun generateDefaultUiState(): MyProfileUiState {
        return MyProfileUiState()
    }

    open fun preserveInputData(inputData: MyProfileInputData) {
        mUiState.myProfileInputData = inputData
    }

    open fun isUpdateDataValid(updateData: MyProfileInputData): Boolean {
        if (updateData.isEmpty()) return false

        if (updateData.password != null) {
            if (updateData.newPassword != updateData.newPasswordAgain) return false

        } else {
            if (updateData.newPassword != null || updateData.newPasswordAgain != null)
                return false
        }

        return true
    }

    fun onMyProfileGetMyProfile(
        getMyProfileResult: GetMyProfileDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        val myProfilePresentation = getMyProfileResult.myProfile?.toMyProfilePresentation()

        if (!getMyProfileResult.isSuccessful()) return onError(getMyProfileResult.error!!)

        mUiState.myProfilePresentation = myProfilePresentation!!

        return listOf(GetMyProfileUiOperation(myProfilePresentation))
    }

    fun onMyProfileUpdateMyProfile(
        updateMyProfileResult: UpdateMyProfileDomainResult
    ): List<UiOperation> {
        val myProfilePresentation = updateMyProfileResult.myProfile?.toMyProfilePresentation()

        if (!updateMyProfileResult.isSuccessful()) return onError(updateMyProfileResult.error!!)

        mUiState.myProfilePresentation = myProfilePresentation!!

        return listOf(UpdateMyProfileUiOperation(myProfilePresentation))
    }

    fun onMyProfileMyProfileUpdated(
        updateMyProfileDomainResult: MyProfileUpdatedDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        if (!updateMyProfileDomainResult.isSuccessful())
            return onError(updateMyProfileDomainResult.error!!)

        mUiState.myProfilePresentation = getUpdatedMyProfilePresentation()

        return listOf(MyProfileUpdatedUiOperation())
    }

    private fun getUpdatedMyProfilePresentation(): MyProfilePresentation {
        val prevMyProfilePresentation = mUiState.myProfilePresentation
            ?: throw IllegalStateException()

        return mUiState.myProfileInputData
            .toUpdatedMyProfilePresentation(prevMyProfilePresentation)
    }

    fun onMyProfileDeleteMyProfile(
        deleteMyProfileDomainResult: DeleteMyProfileDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        if (!deleteMyProfileDomainResult.isSuccessful())
            return onError(deleteMyProfileDomainResult.error!!)

        return listOf(MyProfileDeletedUiOperation())
    }

    fun onMyProfileLogout(
        logoutDomainResult: LogoutDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        if (!logoutDomainResult.isSuccessful())
            return onError(logoutDomainResult.error!!)

        return listOf(LogoutUiOperation())
    }

    open fun getMyProfile() {
        changeLoadingState(true)
        mUseCase.getMyProfile()
    }

    open fun updateMyProfile(updateData: MyProfileInputData) {
        changeLoadingState(true)

        mUiState.myProfileInputData = updateData

        mUseCase.updateMyProfile(updateData.toMyProfileUpdateData())
    }

    open fun deleteMyProfile() {
        changeLoadingState(true)
        mUseCase.deleteMyProfile()
    }

    open fun logout() {
        changeLoadingState(true)
        mUseCase.logout()
    }
}

@Qualifier
annotation class MyProfileViewModelFactoryQualifier

class MyProfileViewModelFactory(
    private val mErrorSource: LocalErrorDataSource,
    private val mMyProfileUseCase: MyProfileUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel(handle, mErrorSource, mMyProfileUseCase) as T
    }
}