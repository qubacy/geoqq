package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state

import com.qubacy.geoqq._common.model.error.Error
import java.io.Serializable

abstract class BaseUiState(
    var error: Error? = null
) : Serializable {
    abstract fun copy(): BaseUiState
}