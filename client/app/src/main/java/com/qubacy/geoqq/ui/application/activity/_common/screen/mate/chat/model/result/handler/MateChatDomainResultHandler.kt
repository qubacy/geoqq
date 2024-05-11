package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModel

class MateChatDomainResultHandler(
    viewModel: MateChatViewModel
) : DomainResultHandler<MateChatViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            GetMessageChunkDomainResult::class -> {
                domainResult as GetMessageChunkDomainResult

                viewModel.onMateChatGetMessageChunk(domainResult)
            }
            UpdateMessageChunkDomainResult::class -> {
                domainResult as UpdateMessageChunkDomainResult

                viewModel.onMateChatUpdateMessageChunk(domainResult)
            }
            DeleteChatDomainResult::class -> {
                domainResult as DeleteChatDomainResult

                viewModel.onMateChatDeleteChat(domainResult)
            }
            else -> listOf()
        }
    }
}