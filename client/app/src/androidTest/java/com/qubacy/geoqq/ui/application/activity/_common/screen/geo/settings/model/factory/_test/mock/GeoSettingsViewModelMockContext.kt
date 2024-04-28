package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.factory._test.mock

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.GeoSettingsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.state.GeoSettingsUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class GeoSettingsViewModelMockContext(
    uiState: GeoSettingsUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var changeLastLocationCallFlag: Boolean = false,
    var setMapLoadingStatusCallFlag: Boolean = false,
    var applyScaleForRadiusCallFlag: Boolean = false
) : ViewModelMockContext<GeoSettingsUiState>(
    uiState, uiOperationFlow, retrieveErrorResult
) {
    override fun reset() {
        super.reset()

        uiState = GeoSettingsUiState(radius = GeoSettingsViewModel.DEFAULT_RADIUS_METERS)

        changeLastLocationCallFlag = false
        setMapLoadingStatusCallFlag = false
        applyScaleForRadiusCallFlag = false
    }
}