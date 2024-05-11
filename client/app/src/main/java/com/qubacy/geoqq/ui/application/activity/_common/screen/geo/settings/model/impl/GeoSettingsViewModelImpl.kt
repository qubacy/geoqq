package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.impl

import android.location.Location
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.extension.changeLoadingState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.extension.preserveLoadingState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.GeoSettingsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.operation.ChangeRadiusUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.state.GeoSettingsUiState
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class GeoSettingsViewModelImpl @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
) : GeoSettingsViewModel(mSavedStateHandle, mErrorSource) {
    companion object {
        const val DEFAULT_RADIUS_METERS = 1000

        const val DEFAULT_MIN_RADIUS = 100
        const val DEFAULT_MAX_RADIUS = 100000
    }

    override fun generateDefaultUiState(): GeoSettingsUiState {
        return GeoSettingsUiState(radius = DEFAULT_RADIUS_METERS)
    }

    override fun preserveLoadingState(isLoading: Boolean) {
        preserveLoadingState(isLoading, mUiState)
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

    override fun setMapLoadingStatus(isLoaded: Boolean) {
        changeLoadingState(!isLoaded) // todo: is it ok?
    }

    override fun applyScaleForRadius(coefficient: Float) {
        val prevRadius = mUiState.radius
        val scaledRadius = getScaledRadius(prevRadius, coefficient)

        mUiState.radius = scaledRadius

        viewModelScope.launch {
            mUiOperationFlow.emit(ChangeRadiusUiOperation(scaledRadius))
        }
    }

    override fun getScaledRadius(radius: Int, coefficient: Float): Int {
        val scaledRadius = (radius * coefficient).toInt()
        val preparedScaledRadius =
            if (scaledRadius < DEFAULT_MIN_RADIUS) DEFAULT_MIN_RADIUS
            else if (scaledRadius > DEFAULT_MAX_RADIUS) DEFAULT_MAX_RADIUS
            else scaledRadius

        return preparedScaledRadius
    }
}

@Qualifier
annotation class GeoSettingsViewModelFactoryQualifier

class GeoSettingsViewModelFactory(
    private val mErrorSource: LocalErrorDatabaseDataSource
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(GeoSettingsViewModelImpl::class.java))
            throw IllegalArgumentException()

        return GeoSettingsViewModelImpl(handle, mErrorSource) as T
    }
}