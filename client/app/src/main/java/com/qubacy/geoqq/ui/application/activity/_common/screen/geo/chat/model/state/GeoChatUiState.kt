package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation

class GeoChatUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    val messages: MutableList<GeoMessagePresentation> = mutableListOf()
) : BusinessUiState(isLoading, error) {
    override fun copy(): GeoChatUiState {
        return GeoChatUiState(isLoading, error, messages.toMutableList())
    }
}