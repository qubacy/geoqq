package com.qubacy.geoqq.ui.screen.mate.chat.model.state

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState

class MateChatUiState(
    val title: String,
    messages: List<Message>,
    users: List<User>,
    newOperations: List<UiOperation>
) : ChatUiState(messages, users, newOperations) {

}