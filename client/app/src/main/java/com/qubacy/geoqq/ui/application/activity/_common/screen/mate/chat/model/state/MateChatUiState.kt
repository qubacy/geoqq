package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation

class MateChatUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    val messageChunkSizes: MutableMap<Int, Int> = mutableMapOf(),
    val messages: MutableList<MateMessagePresentation> = mutableListOf(),
    var newMessageCount: Int = 0,
    var chatContext: MateChatPresentation? = null,
    var isMateRequestSendingAllowed: Boolean = true
) : BusinessUiState(isLoading, error) {
    override fun copy(): MateChatUiState {
        return MateChatUiState(
            isLoading,
            error?.copy(),
            messageChunkSizes.toMutableMap(),
            messages.toMutableList(),
            newMessageCount,
            chatContext?.copy(),
            isMateRequestSendingAllowed
        )
    }
}