package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location

import com.yandex.mapkit.geometry.Point

interface LocationFragment {
    fun onLocationFragmentLocationPointChanged(locationPoint: Point) {
        adjustUiWithLocationPoint(locationPoint)
    }

    fun adjustUiWithLocationPoint(locationPoint: Point) { }
}