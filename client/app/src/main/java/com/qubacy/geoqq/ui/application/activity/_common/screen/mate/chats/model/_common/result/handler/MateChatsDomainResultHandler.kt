package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.get.GetMateChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.update.UpdateMateChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.MateChatsViewModel

class MateChatsDomainResultHandler(
    viewModel: MateChatsViewModel
) : DomainResultHandler<MateChatsViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetMateChatChunkDomainResult::class -> {
                domainResult as GetMateChatChunkDomainResult

                viewModel.onMateChatsGetChatChunk(domainResult)
            }
            UpdateMateChatChunkDomainResult::class -> {
                domainResult as UpdateMateChatChunkDomainResult

                viewModel.onMateChatsUpdateChatChunk(domainResult)
            }
            else -> listOf()
        }
    }
}