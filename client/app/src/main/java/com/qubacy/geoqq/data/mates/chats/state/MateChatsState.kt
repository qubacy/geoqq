package com.qubacy.geoqq.data.mates.chats.state

import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.state.State
import com.qubacy.geoqq.data.mates.chats.entity.MateChatPreview

class MateChatsState(
    val chatPreviews: List<MateChatPreview> = listOf(),
    val users: List<User> = listOf(),
    val requestCount: Int = 0,
    newOperations: List<Operation>
) : State(newOperations) {

}