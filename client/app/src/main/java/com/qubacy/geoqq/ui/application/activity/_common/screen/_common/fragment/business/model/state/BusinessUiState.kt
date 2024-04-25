package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.BaseUiState

abstract class BusinessUiState(
    var isLoading: Boolean = false,
    error: Error? = null
) : BaseUiState(error) {

}