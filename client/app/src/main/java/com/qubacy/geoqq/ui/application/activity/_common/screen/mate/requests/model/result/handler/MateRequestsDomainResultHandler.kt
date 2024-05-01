package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase.result.GetRequestChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModel

class MateRequestsDomainResultHandler(
    viewModel: MateRequestsViewModel
) : DomainResultHandler<MateRequestsViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetRequestChunkDomainResult::class -> {
                domainResult as GetRequestChunkDomainResult

                viewModel.onMateRequestsGetRequestChunk(domainResult)
            }
            AnswerMateRequestDomainResult::class -> {
                domainResult as AnswerMateRequestDomainResult

                viewModel.onMateRequestsAnswerMateRequest(domainResult)
            }
            else -> listOf()
        }
    }
}