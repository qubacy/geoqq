package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.location

import android.location.Location

interface LocationListenerCallback {
    fun onNewLocationGotten(location: Location?)
    fun onLocationServicesNotEnabled()
    fun onRequestingLocationUpdatesFailed(exception: Exception)
}