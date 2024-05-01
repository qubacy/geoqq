package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.operation.handler._common.UiOperationHandler

class LocationUiOperationHandler(
    fragment: LocationFragment
) : UiOperationHandler<LocationFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        if (uiOperation !is LocationPointChangedUiOperation) return false

        fragment.onLocationFragmentLocationPointChanged(uiOperation.locationPoint)

        return true
    }
}