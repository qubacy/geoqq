package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.ChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.model.operation.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.operation.handler._common.UiOperationHandler

class ChatUiOperationHandler(
    fragment: ChatFragment
) : UiOperationHandler<ChatFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        when (uiOperation::class) {
            MateRequestSentToInterlocutorUiOperation::class -> fragment.onChatFragmentMateRequestSent()
            MessageSentUiOperation::class -> fragment.onChatFragmentMessageSent()
            else -> return false
        }

        return true
    }
}