package com.qubacy.geoqq.domain.mate.chats.state

import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.state.State
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

class MateChatsState(
    val chats: List<MateChat> = listOf(),
    val users: List<User> = listOf(),
    val mateRequestCount: Int = 0,
    newOperations: List<Operation> = listOf()
) : State(newOperations) {

}