package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation

class MateRequestsUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    val requests: MutableList<MateRequestPresentation> = mutableListOf(),
    var newRequestCount: Int = 0,
    var answeredRequestCount: Int = 0
) : BusinessUiState(isLoading, error) {
    override fun copy(): MateRequestsUiState {
        return MateRequestsUiState(
            isLoading, error?.copy(), requests.toMutableList(), newRequestCount, answeredRequestCount)
    }
}