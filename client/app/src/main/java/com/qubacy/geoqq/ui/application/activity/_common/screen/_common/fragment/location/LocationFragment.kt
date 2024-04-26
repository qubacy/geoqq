package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation.LocationPointChangedUiOperation

interface LocationFragment {
    fun processLocationPointChangedUiOperation(
        locationPointChangedUiOperation: LocationPointChangedUiOperation
    )
}