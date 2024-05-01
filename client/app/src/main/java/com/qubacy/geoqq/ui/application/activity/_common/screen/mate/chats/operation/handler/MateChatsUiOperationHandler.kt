package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.MateChatsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation

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
            else -> return false
        }

        return true
    }
}