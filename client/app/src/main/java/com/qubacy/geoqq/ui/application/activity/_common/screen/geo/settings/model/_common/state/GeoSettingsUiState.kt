package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.state.LoadingUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState
import com.yandex.mapkit.geometry.Point

class GeoSettingsUiState(
    override var isLoading: Boolean = false,
    error: Error? = null,
    var lastLocationPoint: Point? = null,
    var radius: Int
) : BaseUiState(error), LoadingUiState {
    override fun copy(): GeoSettingsUiState {
        return GeoSettingsUiState(
            isLoading,
            error?.copy(),
            lastLocationPoint?.let { Point(it.latitude, it.longitude) },
            radius
        )
    }
}