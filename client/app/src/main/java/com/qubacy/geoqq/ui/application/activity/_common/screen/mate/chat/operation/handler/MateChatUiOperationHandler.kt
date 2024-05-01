package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.MateChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.request.ChatDeletedUiOperation

class MateChatUiOperationHandler(
    fragment: MateChatFragment
) : UiOperationHandler<MateChatFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        when (uiOperation::class) {
            InsertMessagesUiOperation::class -> {
                uiOperation as InsertMessagesUiOperation

                fragment.onMateChatFragmentInsertMessages(uiOperation.messages, uiOperation.position)
            }
            UpdateMessageChunkUiOperation::class -> {
                uiOperation as UpdateMessageChunkUiOperation

                fragment.onMateChatFragmentUpdateMessages(
                    uiOperation.messages, uiOperation.position, uiOperation.messageChunkSizeDelta)
            }
            ChatDeletedUiOperation::class -> fragment.onMateChatFragmentChatDeleted()
            else -> return false
        }

        return true
    }
}