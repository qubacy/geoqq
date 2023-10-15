package com.qubacy.geoqq.ui.screen.mate.chats.model.state

import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.ui.common.fragment.common.model.state.BaseUiState

// chats have to be ordered the way they will be shown in the list (0: newest, .., N - 1: oldest)!!!

class MateChatsUiState(
    val chats: MutableList<Chat> = mutableListOf()
) : BaseUiState(null) {

}