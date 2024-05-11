package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.LoadingViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.model.LocationViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.state.GeoSettingsUiState

abstract class GeoSettingsViewModel(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
) : StatefulViewModel<GeoSettingsUiState>(
    mSavedStateHandle, mErrorSource
), LoadingViewModel, LocationViewModel {
    abstract fun setMapLoadingStatus(isLoaded: Boolean)
    abstract fun applyScaleForRadius(coefficient: Float)
    abstract fun getScaledRadius(radius: Int, coefficient: Float): Int
}