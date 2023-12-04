package com.qubacy.geoqq.domain.common.state.chat

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.common.State
import com.qubacy.geoqq.domain.common.model.message.Message

abstract class ChatState(
    val messages: List<Message>,
    val users: List<User>,
    newOperations: List<Operation>
) : State(newOperations) {

}