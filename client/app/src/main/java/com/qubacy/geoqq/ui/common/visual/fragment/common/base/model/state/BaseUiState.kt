package com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.state

import com.qubacy.geoqq.common.error.common.Error

abstract class BaseUiState(
    val error: Error? = null
) : UiState() {

}