package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model

import android.location.Location
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.LoadingViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.extension.changeLoadingState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.state.GeoSettingsUiState
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class GeoSettingsViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository
) : StatefulViewModel<GeoSettingsUiState>(
    mSavedStateHandle, mErrorDataRepository
), LoadingViewModel, LocationViewModel {
    override fun generateDefaultUiState(): GeoSettingsUiState {
        return GeoSettingsUiState()
    }

    override fun changeLoadingState(isLoading: Boolean) {
        changeLoadingState(isLoading, mUiState, mUiOperationFlow)
    }

    override fun changeLastLocation(newLocation: Location) {
        val locationPoint = Point(newLocation.latitude, newLocation.longitude)

        if (locationPoint == uiState.lastLocationPoint) return

        mUiState.lastLocationPoint = locationPoint

        viewModelScope.launch {
            mUiOperationFlow.emit(LocationPointChangedUiOperation(locationPoint))
        }
    }
}

@Qualifier
annotation class GeoSettingsViewModelFactoryQualifier

class GeoSettingsViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(GeoSettingsViewModel::class.java))
            throw IllegalArgumentException()

        return GeoSettingsViewModel(handle, mErrorDataRepository) as T
    }
}