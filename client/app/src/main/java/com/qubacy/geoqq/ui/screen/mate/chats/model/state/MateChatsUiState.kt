package com.qubacy.geoqq.ui.screen.mate.chats.model.state

import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.state.OperationUiState

// chats have to be ordered the way they will be shown in the list (0: newest, .., N - 1: oldest)!!!

class MateChatsUiState(
    val chats: List<Chat>,
    val users: List<User>,
    val requestCount: Int,
    uiOperations: List<UiOperation>
) : OperationUiState(uiOperations) {

}