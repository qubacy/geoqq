package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.model

import android.location.Location

interface LocationViewModel {
    fun changeLastLocation(newLocation: Location)
}