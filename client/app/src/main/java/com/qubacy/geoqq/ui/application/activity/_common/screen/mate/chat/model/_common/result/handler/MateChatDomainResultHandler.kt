package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.delete.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.message.MateMessageAddedDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.MateChatViewModel

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
            MateMessageAddedDomainResult::class -> {
                domainResult as MateMessageAddedDomainResult

                viewModel.onMateChatMessageAdded(domainResult)
            }
            SendMessageDomainResult::class -> {
                domainResult as SendMessageDomainResult

                viewModel.onMateChatSendMessage(domainResult)
            }
            else -> listOf()
        }
    }
}