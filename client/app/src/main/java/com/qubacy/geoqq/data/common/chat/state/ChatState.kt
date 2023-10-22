package com.qubacy.geoqq.data.common.chat.state

import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.state.State

open class ChatState(
    val chat: Chat = Chat(),
    val messages: List<Message> = listOf(),
    val users: List<User> = listOf(),
    newOperations: List<Operation>
) : State(newOperations) {

}