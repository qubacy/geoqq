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
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.operation.ChangeRadiusUiOperation
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
    companion object {
        const val DEFAULT_RADIUS_METERS = 1000f
    }

    override fun generateDefaultUiState(): GeoSettingsUiState {
        return GeoSettingsUiState(radius = DEFAULT_RADIUS_METERS)
    }

    override fun changeLoadingState(isLoading: Boolean) {
        changeLoadingState(isLoading, mUiState, mUiOperationFlow)
    }

    override fun changeLastLocation(newLocation: Location) {
        val prevLocationPoint = mUiState.lastLocationPoint

        if (prevLocationPoint != null
            && (prevLocationPoint.latitude == newLocation.latitude
            && prevLocationPoint.longitude == newLocation.longitude
        )) {
            return
        }

        val locationPoint = Point(newLocation.latitude, newLocation.longitude)

        mUiState.lastLocationPoint = locationPoint

        viewModelScope.launch {
            mUiOperationFlow.emit(LocationPointChangedUiOperation(locationPoint))
        }
    }

    fun setMapLoadingStatus(isLoaded: Boolean) {
        changeLoadingState(!isLoaded) // todo: is it ok?
    }

    fun applyScaleForRadius(coefficient: Float) {
        val prevRadius = mUiState.radius

        // todo: calculate the value according to the formulas..

        // todo: delete:
        viewModelScope.launch {
            mUiOperationFlow.emit(ChangeRadiusUiOperation(prevRadius * coefficient))
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