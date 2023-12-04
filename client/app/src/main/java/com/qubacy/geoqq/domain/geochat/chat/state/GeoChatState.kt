package com.qubacy.geoqq.domain.geochat.chat.state

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.chat.ChatState

class GeoChatState(
    messages: List<Message> = listOf(),
    users: List<User> = listOf(),
    newOperations: List<Operation> = listOf()
) : ChatState(messages, users, newOperations) {

}