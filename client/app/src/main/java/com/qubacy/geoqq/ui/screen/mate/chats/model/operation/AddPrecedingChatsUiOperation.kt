package com.qubacy.geoqq.ui.screen.mate.chats.model.operation

import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation

class AddPrecedingChatsUiOperation(
    val precedingChats: List<MateChat>
) : UiOperation() {

}