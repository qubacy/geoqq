package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.MateChatsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chat.add.AddChatUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chat.delete.DeleteChatUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chat.insert.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chat.update.UpdateChatUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chunk.update.UpdateChatChunkUiOperation

class MateChatsUiOperationHandler(
    fragment: MateChatsFragment
) : UiOperationHandler<MateChatsFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        when (uiOperation::class) {
            InsertChatsUiOperation::class -> {
                uiOperation as InsertChatsUiOperation

                fragment.onMateChatsFragmentInsertChats(uiOperation.chats, uiOperation.position)
            }
            UpdateChatChunkUiOperation::class -> {
                uiOperation as UpdateChatChunkUiOperation

                fragment.onMateChatsFragmentUpdateChats(
                    uiOperation.chats, uiOperation.position, uiOperation.chatChunkSizeDelta)
            }
            AddChatUiOperation::class -> {
                uiOperation as AddChatUiOperation

                fragment.onMateChatsFragmentAddChat(uiOperation.chat, uiOperation.position)
            }
            UpdateChatUiOperation::class -> {
                uiOperation as UpdateChatUiOperation

                fragment.onMateChatsFragmentUpdateChat(
                    uiOperation.chat, uiOperation.prevPosition, uiOperation.position)
            }
            DeleteChatUiOperation::class -> {
                uiOperation as DeleteChatUiOperation

                fragment.onMateChatsFragmentDeleteChat(uiOperation.position)
            }
            else -> return false
        }

        return true
    }
}