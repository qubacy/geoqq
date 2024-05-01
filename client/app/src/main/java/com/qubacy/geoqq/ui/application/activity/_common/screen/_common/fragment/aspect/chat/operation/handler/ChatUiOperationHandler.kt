package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.ChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler

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