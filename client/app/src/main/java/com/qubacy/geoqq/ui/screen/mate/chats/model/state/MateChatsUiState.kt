package com.qubacy.geoqq.ui.screen.mate.chats.model.state

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.state.OperationUiState

// chats have to be ordered the way they will be shown in the list (0: newest, .., N - 1: oldest)!!!

class MateChatsUiState(
    val chats: List<MateChat>,
    val users: List<User>,
    val requestCount: Int,
    uiOperations: List<UiOperation>
) : OperationUiState(uiOperations) {

}