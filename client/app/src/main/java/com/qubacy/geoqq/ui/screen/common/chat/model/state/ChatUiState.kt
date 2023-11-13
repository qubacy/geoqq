package com.qubacy.geoqq.ui.screen.common.chat.model.state

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.state.OperationUiState

abstract class ChatUiState(
    val messages: List<Message>,
    val users: List<User>,
    newUiOperations: List<UiOperation>
) : OperationUiState(newUiOperations) {

}