package com.qubacy.geoqq.ui.screen.common.chat.model.state.operation

import com.qubacy.geoqq.data.common.entity.message.Message

class SetMessagesUiOperation(
    val messages: List<Message>,
) : ChatUiOperation {
}