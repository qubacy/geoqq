package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.newer.NewGeoMessagesDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.GeoChatViewModel

class GeoChatDomainResultHandler(
    viewModel: GeoChatViewModel
) : DomainResultHandler<GeoChatViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetGeoMessagesDomainResult::class -> {
                domainResult as GetGeoMessagesDomainResult

                viewModel.onGeoChatGetGeoMessages(domainResult)
            }
            NewGeoMessagesDomainResult::class -> {
                domainResult as NewGeoMessagesDomainResult

                viewModel.onGeoChatNewGeoMessages(domainResult)
            }
            else -> listOf()
        }
    }
}