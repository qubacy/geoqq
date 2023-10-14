package com.qubacy.geoqq.ui.screen.common.chat.model.state.operation

import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation

class SetMessagesUiOperation(
    val messages: List<Message>
) : UiOperation() {

}