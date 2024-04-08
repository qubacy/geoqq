package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation

class MateChatUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    val messageChunks: MutableMap<Int, List<MateMessagePresentation>> = mutableMapOf(),
    var interlocutor: UserPresentation? = null
) : BusinessUiState(isLoading, error) {
    override fun copy(): MateChatUiState {
        return MateChatUiState(
            isLoading,
            error?.copy(),
            messageChunks.toMutableMap(),
            interlocutor?.copy()
        )
    }
}