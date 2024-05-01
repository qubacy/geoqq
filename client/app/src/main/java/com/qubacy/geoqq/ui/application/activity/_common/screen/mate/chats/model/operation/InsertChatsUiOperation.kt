package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation

class InsertChatsUiOperation(
    val position: Int,
    val chats: List<MateChatPresentation>
) : UiOperation {

}