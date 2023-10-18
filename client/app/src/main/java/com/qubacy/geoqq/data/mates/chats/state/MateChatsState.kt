package com.qubacy.geoqq.data.mates.chats.state

import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.state.State

class MateChatsState(
    val chats: List<Chat> = listOf(),
    val users: List<User> = listOf(),
    val requestCount: Int = 0,
    newOperations: List<Operation>
) : State(newOperations) {

}