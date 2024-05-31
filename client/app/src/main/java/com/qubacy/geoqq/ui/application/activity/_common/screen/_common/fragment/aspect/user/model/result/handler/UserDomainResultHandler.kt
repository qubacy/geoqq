package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model.UserViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation

class UserDomainResultHandler(
    viewModel: UserViewModel
) : DomainResultHandler<UserViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetUserDomainResult::class -> {
                domainResult as GetUserDomainResult

                viewModel.onUserGetUser(domainResult)
            }
            UpdateUserDomainResult::class -> {
                domainResult as UpdateUserDomainResult

                viewModel.onUserUpdateUser(domainResult)
            }
            else -> listOf()
        }
    }
}