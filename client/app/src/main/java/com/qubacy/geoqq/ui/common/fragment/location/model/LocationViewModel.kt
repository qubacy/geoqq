package com.qubacy.geoqq.ui.common.fragment.location.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.yandex.mapkit.geometry.Point

abstract class LocationViewModel(

) : WaitingViewModel() {
    companion object {
        const val TAG = "LOCATION_VIEW_MODEL"
    }

    private val mLastLocationPoint: MutableLiveData<Point?> = MutableLiveData(null)
    val lastLocationPoint: LiveData<Point?> = mLastLocationPoint

    // Note: the return Boolean value allows to organize a conveyed checking
    //       through the whole ViewModel hierarchy. So for inheritants it's
    //       possible to rely on the return of the parents' method's
    //       implementation;

    open fun changeLastLocation(location: Location): Boolean {
        if (location == mLastLocationPoint.value) return false

        val locationPoint = Point(location.latitude, location.longitude)

        mLastLocationPoint.value = locationPoint

        return true
    }
}