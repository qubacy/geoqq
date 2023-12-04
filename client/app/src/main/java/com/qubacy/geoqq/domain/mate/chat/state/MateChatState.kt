package com.qubacy.geoqq.domain.mate.chat.state

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.state.chat.ChatState

class MateChatState(
    messages: List<Message> = listOf(),
    users: List<User> = listOf(),
    newOperations: List<Operation> = listOf()
) : ChatState(messages, users, newOperations) {

}