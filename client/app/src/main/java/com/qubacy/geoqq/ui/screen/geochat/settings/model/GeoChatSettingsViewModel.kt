package com.qubacy.geoqq.ui.screen.geochat.settings.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.model.WaitingViewModel

class GeoChatSettingsViewModel : WaitingViewModel() {
    companion object {
        const val TAG = "SETTINGS_VIEW_MODEL"

        val RADIUS_OPTION_INDEX_TO_METERS_ARRAY = floatArrayOf(
            250f, 500f, 1000f, 3000f, 10000f
        )
    }

    private val mCurRadiusOptionIndex = MutableLiveData<Int>(0)
    val curRadiusOptionIndex: LiveData<Int> = mCurRadiusOptionIndex

    fun changeCurRadiusOptionIndex(index: Int) {
        if (index >= RADIUS_OPTION_INDEX_TO_METERS_ARRAY.size || index < 0)
            throw IndexOutOfBoundsException()

        mCurRadiusOptionIndex.value = index
    }

    fun getCurRadiusOptionMeters(): Float {
        return RADIUS_OPTION_INDEX_TO_METERS_ARRAY[curRadiusOptionIndex.value!!]
    }
}

class GeoChatSettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(GeoChatSettingsViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatSettingsViewModel() as T
    }
}