package com.qubacy.geoqq.ui.screen.geochat.chat.model.state

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.state.ChatUiState

class GeoChatUiState(
    messages: List<Message>,
    users: List<User>,
    newUiOperations: List<UiOperation>
) : ChatUiState(messages, users, newUiOperations) {

}