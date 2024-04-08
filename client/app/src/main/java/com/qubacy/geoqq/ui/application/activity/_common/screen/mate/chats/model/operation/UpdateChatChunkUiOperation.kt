package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation

class UpdateChatChunkUiOperation(
    val position: Int,
    val chats: List<MateChatPresentation>,
    val chatChunkSizeDelta: Int = 0
) : UiOperation {

}