package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation

class MateChatsUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    val chats: MutableList<MateChatPresentation> = mutableListOf(),
    val chatChunkSizes: MutableMap<Int, Int> = mutableMapOf(),
    var affectedChatCount: Int = 0
) : BusinessUiState(isLoading, error) {
    override fun copy(): MateChatsUiState {
        return MateChatsUiState(
            isLoading, error?.copy(), chats.toMutableList(), chatChunkSizes.toMutableMap())
    }
}