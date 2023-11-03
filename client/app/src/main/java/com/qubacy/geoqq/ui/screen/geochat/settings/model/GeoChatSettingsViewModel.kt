package com.qubacy.geoqq.ui.screen.geochat.settings.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data.geochat.settings.GeoChatSettingsContext
import com.qubacy.geoqq.ui.common.fragment.location.model.LocationViewModel

class GeoChatSettingsViewModel(

) : LocationViewModel() {
    companion object {
        const val TAG = "SETTINGS_VIEW_MODEL"

        const val METERS_IN_KM_COUNT = 1000

        const val METERS_POSTFIX = " m"
        const val KILOMETERS_POSTFIX = " km"
    }

    private val mCurRadiusOptionIndex = MutableLiveData<Int>(0)
    val curRadiusOptionIndex: LiveData<Int> = mCurRadiusOptionIndex

    fun changeCurRadiusOptionIndex(index: Int) {
        if (index >= GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY.size || index < 0)
            throw IndexOutOfBoundsException()

        mCurRadiusOptionIndex.value = index
    }

    fun getCurRadiusOptionMeters(): Float {
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
        TODO("Not yet implemented")
    }
}

class GeoChatSettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatSettingsViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatSettingsViewModel() as T
    }
}