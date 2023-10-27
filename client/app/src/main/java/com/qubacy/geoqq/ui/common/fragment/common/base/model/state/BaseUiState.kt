package com.qubacy.geoqq.ui.common.fragment.common.base.model.state

import com.qubacy.geoqq.common.error.local.LocalError

abstract class BaseUiState(
    val error: LocalError? = null
) : UiState() {

}