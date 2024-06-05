package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation

class GeoChatUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    val messages: MutableList<GeoMessagePresentation> = mutableListOf(),
    var isMessageSendingAllowed: Boolean = false
) : BusinessUiState(isLoading, error) {
    override fun copy(): GeoChatUiState {
        return GeoChatUiState(isLoading, error, messages.toMutableList(), isMessageSendingAllowed)
    }
}