package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.logout.usecase.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.MyProfileViewModel

class MyProfileDomainResultHandler(
    viewModel: MyProfileViewModel
) : DomainResultHandler<MyProfileViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetMyProfileDomainResult::class -> {
                domainResult as GetMyProfileDomainResult

                viewModel.onMyProfileGetMyProfile(domainResult)
            }
            UpdateMyProfileDomainResult::class -> {
                domainResult as UpdateMyProfileDomainResult

                viewModel.onMyProfileUpdateMyProfile(domainResult)
            }
            DeleteMyProfileDomainResult::class -> {
                domainResult as DeleteMyProfileDomainResult

                viewModel.onMyProfileDeleteMyProfile(domainResult)
            }
            LogoutDomainResult::class -> {
                domainResult as LogoutDomainResult

                viewModel.onMyProfileLogout(domainResult)
            }
            else -> listOf()
        }
    }
}