package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model

import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation

interface ChatViewModel {
    fun onChatSendMateRequest(domainResult: SendMateRequestDomainResult): List<UiOperation> {
        val businessViewModel = getChatViewModelBusinessViewModel()

        businessViewModel.changeLoadingState(false)

        if (!domainResult.isSuccessful())
            return businessViewModel.onError(domainResult.error!!)

        return listOf(MateRequestSentToInterlocutorUiOperation())
    }

    fun onChatSendMessage(domainResult: SendMessageDomainResult): List<UiOperation> {
        val businessViewModel = getChatViewModelBusinessViewModel()

        businessViewModel.changeLoadingState(false)

        if (!domainResult.isSuccessful())
            return businessViewModel.onError(domainResult.error!!)

        return listOf(MessageSentUiOperation())
    }

    fun getChatViewModelBusinessViewModel(): BusinessViewModel<*, *>
}