package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.InterlocutorViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation

class InterlocutorDomainResultHandler(
    viewModel: InterlocutorViewModel
) : DomainResultHandler<InterlocutorViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetUserDomainResult::class -> {
                domainResult as GetUserDomainResult

                viewModel.onInterlocutorGetInterlocutor(domainResult)
            }
            UpdateUserDomainResult::class -> {
                domainResult as UpdateUserDomainResult

                viewModel.onInterlocutorUpdateInterlocutor(domainResult)
            }
            else -> listOf()
        }
    }
}