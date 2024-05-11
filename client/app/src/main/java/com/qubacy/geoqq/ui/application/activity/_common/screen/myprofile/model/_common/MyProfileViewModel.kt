package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.logout.usecase._common.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.update.MyProfileUpdatedDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.result.handler.MyProfileDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.MyProfileUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.input.MyProfileInputData

abstract class MyProfileViewModel(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mMyProfileUseCase: MyProfileUseCase
) : BusinessViewModel<MyProfileUiState, MyProfileUseCase>(
    mSavedStateHandle, mErrorSource, mMyProfileUseCase
), AuthorizedViewModel {
    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(AuthorizedDomainResultHandler(this))
            .plus(MyProfileDomainResultHandler(this))
    }
    open fun preserveInputData(inputData: MyProfileInputData) {
        mUiState.myProfileInputData = inputData
    }
    override fun generateDefaultUiState(): MyProfileUiState {
        return MyProfileUiState()
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
    abstract fun onMyProfileGetMyProfile(
        getMyProfileResult: GetMyProfileDomainResult
    ): List<UiOperation>
    abstract fun onMyProfileUpdateMyProfile(
        updateMyProfileResult: UpdateMyProfileDomainResult
    ): List<UiOperation>
    abstract fun onMyProfileMyProfileUpdated(
        updateMyProfileDomainResult: MyProfileUpdatedDomainResult
    ): List<UiOperation>
    abstract fun onMyProfileDeleteMyProfile(
        deleteMyProfileDomainResult: DeleteMyProfileDomainResult
    ): List<UiOperation>
    abstract fun onMyProfileLogout(
        logoutDomainResult: LogoutDomainResult
    ): List<UiOperation>
    abstract fun getMyProfile()
    abstract fun updateMyProfile(updateData: MyProfileInputData)
    abstract fun deleteMyProfile()
    abstract fun logout()
}