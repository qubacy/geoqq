package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.state.LoadingUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState

abstract class BusinessUiState(
    override var isLoading: Boolean = false,
    error: Error? = null
) : BaseUiState(error), LoadingUiState {

}