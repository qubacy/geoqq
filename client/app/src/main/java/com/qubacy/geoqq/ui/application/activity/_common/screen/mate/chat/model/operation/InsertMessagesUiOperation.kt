package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation

class InsertMessagesUiOperation(
    val position: Int,
    val messages: List<MateMessagePresentation>
) : UiOperation {

}