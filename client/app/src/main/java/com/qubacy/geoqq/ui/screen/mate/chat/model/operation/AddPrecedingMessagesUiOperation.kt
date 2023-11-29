package com.qubacy.geoqq.ui.screen.mate.chat.model.operation

import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation

class AddPrecedingMessagesUiOperation(
    val precedingMessages: List<Message>
) : UiOperation() {

}