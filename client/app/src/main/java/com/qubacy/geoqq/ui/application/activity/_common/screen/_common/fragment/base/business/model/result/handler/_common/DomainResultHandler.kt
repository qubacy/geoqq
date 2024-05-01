package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation

abstract class DomainResultHandler<ViewModelType>(
    val viewModel: ViewModelType
) {
    abstract fun handleDomainResult(domainResult: DomainResult): List<UiOperation>
}