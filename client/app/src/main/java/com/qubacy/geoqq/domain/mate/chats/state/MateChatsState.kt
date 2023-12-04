package com.qubacy.geoqq.domain.mate.chats.state

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.common.State
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

class MateChatsState(
    val chats: List<MateChat> = listOf(),
    val users: List<User> = listOf(),
    val mateRequestCount: Int = 0,
    newOperations: List<Operation> = listOf()
) : State(newOperations) {

}