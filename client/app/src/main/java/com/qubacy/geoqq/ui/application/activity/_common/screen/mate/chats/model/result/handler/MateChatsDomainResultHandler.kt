package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.UpdateChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel

class MateChatsDomainResultHandler(
    viewModel: MateChatsViewModel
) : DomainResultHandler<MateChatsViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetChatChunkDomainResult::class -> {
                domainResult as GetChatChunkDomainResult

                viewModel.onMateChatsGetChatChunk(domainResult)
            }
            UpdateChatChunkDomainResult::class -> {
                domainResult as UpdateChatChunkDomainResult

                viewModel.onMateChatsUpdateChatChunk(domainResult)
            }
            else -> listOf()
        }
    }
}