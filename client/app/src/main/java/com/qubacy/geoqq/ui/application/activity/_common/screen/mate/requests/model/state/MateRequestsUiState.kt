package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState

class MateRequestsUiState(
    isLoading: Boolean = false,
    error: Error? = null,

) : BusinessUiState(isLoading, error) {
    override fun copy(): MateRequestsUiState {
        return MateRequestsUiState(isLoading, error?.copy())
    }
}