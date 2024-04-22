package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation

class MateChatsUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    val chats: MutableList<MateChatPresentation> = mutableListOf(),
    val chatChunkSizes: MutableList<Int> = mutableListOf(),
    var newChatCount: Int = 0
) : BusinessUiState(isLoading, error) {
    override fun copy(): MateChatsUiState {
        return MateChatsUiState(
            isLoading, error?.copy(), chats.toMutableList(), chatChunkSizes.toMutableList())
    }
}