package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.GeoSettingsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.operation.ChangeRadiusUiOperation

class GeoSettingsUiOperationHandler(
    fragment: GeoSettingsFragment
) : UiOperationHandler<GeoSettingsFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        if (uiOperation !is ChangeRadiusUiOperation) return false

        fragment.onGeoSettingsFragmentChangeRadius(uiOperation.radius)

        return true
    }
}