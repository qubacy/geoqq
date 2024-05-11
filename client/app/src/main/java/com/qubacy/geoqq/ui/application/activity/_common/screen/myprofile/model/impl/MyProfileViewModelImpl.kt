package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.impl

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.logout.usecase._common.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.update.MyProfileUpdatedDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.MyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.toMyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.MyProfileDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.profile.get.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.MyProfileViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.MyProfileUpdatedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.profile.update.UpdateMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.input.MyProfileInputData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.input.toMyProfileUpdateData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.input.toUpdatedMyProfilePresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MyProfileViewModelImpl @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mMyProfileUseCase: MyProfileUseCase
) : MyProfileViewModel(mSavedStateHandle, mErrorSource, mMyProfileUseCase) {
    companion object {
        const val TAG = "MyProfileViewModel"
    }

    override fun onMyProfileGetMyProfile(
        getMyProfileResult: GetMyProfileDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        val myProfilePresentation = getMyProfileResult.myProfile?.toMyProfilePresentation()

        if (!getMyProfileResult.isSuccessful()) return onError(getMyProfileResult.error!!)

        mUiState.myProfilePresentation = myProfilePresentation!!

        return listOf(GetMyProfileUiOperation(myProfilePresentation))
    }

    override fun onMyProfileUpdateMyProfile(
        updateMyProfileResult: UpdateMyProfileDomainResult
    ): List<UiOperation> {
        val myProfilePresentation = updateMyProfileResult.myProfile?.toMyProfilePresentation()

        if (!updateMyProfileResult.isSuccessful()) return onError(updateMyProfileResult.error!!)

        mUiState.myProfilePresentation = myProfilePresentation!!

        return listOf(UpdateMyProfileUiOperation(myProfilePresentation))
    }

    override fun onMyProfileMyProfileUpdated(
        updateMyProfileDomainResult: MyProfileUpdatedDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        if (!updateMyProfileDomainResult.isSuccessful())
            return onError(updateMyProfileDomainResult.error!!)

        mUiState.myProfilePresentation = getUpdatedMyProfilePresentation()

        return listOf(MyProfileUpdatedUiOperation())
    }

    override fun onMyProfileDeleteMyProfile(
        deleteMyProfileDomainResult: DeleteMyProfileDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        if (!deleteMyProfileDomainResult.isSuccessful())
            return onError(deleteMyProfileDomainResult.error!!)

        return listOf(MyProfileDeletedUiOperation())
    }

    override fun onMyProfileLogout(
        logoutDomainResult: LogoutDomainResult
    ): List<UiOperation> {
        changeLoadingState(false)

        if (!logoutDomainResult.isSuccessful())
            return onError(logoutDomainResult.error!!)

        return listOf(LogoutUiOperation())
    }

    override fun getMyProfile() {
        changeLoadingState(true)
        mUseCase.getMyProfile()
    }

    override fun updateMyProfile(updateData: MyProfileInputData) {
        changeLoadingState(true)

        mUiState.myProfileInputData = updateData

        mUseCase.updateMyProfile(updateData.toMyProfileUpdateData())
    }

    override fun deleteMyProfile() {
        changeLoadingState(true)
        mUseCase.deleteMyProfile()
    }

    override fun logout() {
        changeLoadingState(true)
        mUseCase.logout()
    }

    private fun getUpdatedMyProfilePresentation(): MyProfilePresentation {
        val prevMyProfilePresentation = mUiState.myProfilePresentation
            ?: throw IllegalStateException()

        return mUiState.myProfileInputData
            .toUpdatedMyProfilePresentation(prevMyProfilePresentation)
    }
}

@Qualifier
annotation class MyProfileViewModelFactoryQualifier

class MyProfileViewModelImplFactory(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mMyProfileUseCase: MyProfileUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModelImpl::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModelImpl(handle, mErrorSource, mMyProfileUseCase) as T
    }
}