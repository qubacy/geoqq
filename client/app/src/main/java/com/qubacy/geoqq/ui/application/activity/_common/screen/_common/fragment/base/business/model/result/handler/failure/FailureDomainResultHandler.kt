package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler.failure

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase._common.result.failure.FailureDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation

class FailureDomainResultHandler(
    viewModel: BusinessViewModel<*, *>
) : DomainResultHandler<BusinessViewModel<*, *>>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        if (domainResult !is FailureDomainResult) return emptyList()

        return viewModel.onError(domainResult.error!!)
    }
}