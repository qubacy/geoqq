package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.result.error.ErrorWithLogoutDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation

class AuthorizedDomainResultHandler(
    viewModel: AuthorizedViewModel
) : DomainResultHandler<AuthorizedViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        if (domainResult !is ErrorWithLogoutDomainResult) return listOf()

        return viewModel.onAuthorizedErrorWithLogout(domainResult)
    }
}