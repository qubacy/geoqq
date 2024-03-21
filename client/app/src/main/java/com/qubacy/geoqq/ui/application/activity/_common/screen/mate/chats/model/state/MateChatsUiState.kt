package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState

class MateChatsUiState(
    isLoading: Boolean = false,
    error: Error? = null,

) : BusinessUiState(isLoading, error) {
    override fun copy(): MateChatsUiState {
        return MateChatsUiState(isLoading, error?.copy(), )
    }
}