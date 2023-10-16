package com.qubacy.geoqq.ui.common.fragment.common.model.state

import com.qubacy.geoqq.common.error.Error

abstract class BaseUiState(
    val error: Error? = null
) : UiState() {

}