package com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.login.usecase._common.result.SignedInDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.LoginViewModel

class LoginDomainResultHandler(
    viewModel: LoginViewModel
) : DomainResultHandler<LoginViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        if (domainResult !is SignedInDomainResult) return listOf()

        return viewModel.onLoginSignedIn(domainResult)
    }
}