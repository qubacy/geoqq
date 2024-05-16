package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.result.handler

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation

class ChatDomainResultHandler(
    viewModel: ChatViewModel
) : DomainResultHandler<ChatViewModel>(viewModel) {
    override fun handleDomainResult(domainResult: DomainResult): List<UiOperation> {
        return when (domainResult::class) {
            SendMateRequestDomainResult::class -> {
                domainResult as SendMateRequestDomainResult

                viewModel.onChatSendMateRequest(domainResult)
            }
            SendMessageDomainResult::class -> {
                domainResult as SendMessageDomainResult

                viewModel.onChatSendMessage(domainResult)
            }
            else -> listOf()
        }
    }
}