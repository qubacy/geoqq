package com.qubacy.geoqq.ui.screen.geochat.settings.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.qubacy.geoqq.data.geochat.settings.GeoChatSettingsContext
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.geochat.settings.GeoChatSettingsUseCase
import com.qubacy.geoqq.domain.geochat.settings.state.GeoChatSettingsState
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.state.GeoChatSettingsUiState
import kotlinx.coroutines.flow.map

open class GeoChatSettingsViewModel(
    private val mGeoChatSettingsUseCase: GeoChatSettingsUseCase
) : LocationViewModel() {
    companion object {
        const val TAG = "SETTINGS_VIEW_MODEL"

        const val METERS_IN_KM_COUNT = 1000

        const val METERS_POSTFIX = " m"
        const val KILOMETERS_POSTFIX = " km"
    }

    private val mGeoChatStateFlow = mGeoChatSettingsUseCase.stateFlow

    private val mGeoChatSettingsUiStateFlow = mGeoChatStateFlow.map { stateToUiState(it) }
    val geoChatSettingsUiStateFlow: LiveData<GeoChatSettingsUiState?> =
        mGeoChatSettingsUiStateFlow.asLiveData()

    private val mCurRadiusOptionIndex = MutableLiveData<Int>(0)
    val curRadiusOptionIndex: LiveData<Int> = mCurRadiusOptionIndex

    val isInitializing: Boolean get() = lastLocationPoint.isInitialized

    private fun stateToUiState(geoChatState: GeoChatSettingsState?): GeoChatSettingsUiState? {
        if (geoChatState == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in geoChatState.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return GeoChatSettingsUiState(uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation {
        return when(operation::class) {
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> { throw IllegalStateException() }
        }
    }

    fun changeCurRadiusOptionIndex(index: Int) {
        if (index >= GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY.size || index < 0)
            throw IndexOutOfBoundsException()

        mCurRadiusOptionIndex.value = index
    }

    fun getCurRadiusOptionMeters(): Int {
        return GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY[curRadiusOptionIndex.value!!]
    }

    fun getLabelForRadiusOption(radiusOption: Int): String {
        val resultString = StringBuilder("")
        val radiusInMeters = GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY[radiusOption].toInt()

        if (radiusInMeters >= METERS_IN_KM_COUNT)
            resultString.append(radiusInMeters / METERS_IN_KM_COUNT).append(KILOMETERS_POSTFIX)
        else
            resultString.append(radiusInMeters).append(METERS_POSTFIX)

        return resultString.toString()
    }

    fun onMapLoadingStarted() {
        mIsWaiting.value = true
    }

    fun onMapLoadingStopped() {
        mIsWaiting.value = false
    }

    override fun retrieveError(errorId: Long) {
        mGeoChatSettingsUseCase.getError(errorId)
    }
}

open class GeoChatSettingsViewModelFactory(
    private val mGeoChatSettingsUseCase: GeoChatSettingsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatSettingsViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatSettingsViewModel(mGeoChatSettingsUseCase) as T
    }
}