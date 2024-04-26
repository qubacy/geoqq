package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model

import com.yandex.mapkit.location.Location

interface LocationViewModel {
    fun changeLastLocation(newLocation: Location?)
}