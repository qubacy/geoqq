package com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation

import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation

class SetChatsUiOperation(
    val chats: List<Chat>
) : UiOperation() {

}