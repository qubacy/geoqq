package com.qubacy.geoqq.ui.common.fragment.location.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.yandex.mapkit.geometry.Point

abstract class LocationViewModel : WaitingViewModel() {
    companion object {
        const val TAG = "LOCATION_VIEW_MODEL"
    }

    private val mLastLocationPoint: MutableLiveData<Point?> = MutableLiveData(null)
    val lastLocationPoint: LiveData<Point?> = mLastLocationPoint

    fun changeLastLocation(location: Location) {
        if (location == mLastLocationPoint.value) return

        val locationPoint = Point(location.latitude, location.longitude)

        mLastLocationPoint.value = locationPoint
    }
}