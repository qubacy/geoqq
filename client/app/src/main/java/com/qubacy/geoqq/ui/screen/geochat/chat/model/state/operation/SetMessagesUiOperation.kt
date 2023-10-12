package com.qubacy.geoqq.ui.screen.geochat.chat.model.state.operation

import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User

class SetMessagesUiOperation(
    val messages: List<Message>,
) : GeoChatUiOperation {
}