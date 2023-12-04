package com.qubacy.geoqq.ui.common.visual.fragment.chat.model.state

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.state.OperationUiState

abstract class ChatUiState(
    val messages: List<Message>,
    val users: List<User>,
    newUiOperations: List<UiOperation>
) : OperationUiState(newUiOperations) {

}