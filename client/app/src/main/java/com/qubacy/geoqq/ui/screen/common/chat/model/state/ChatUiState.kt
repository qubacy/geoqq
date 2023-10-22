package com.qubacy.geoqq.ui.screen.common.chat.model.state

import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.state.OperationUiState

class ChatUiState(
    val chat: Chat,
    val messages: List<Message>,
    val users: List<User>,
    newUiOperations: List<UiOperation>
) : OperationUiState(newUiOperations) {

}