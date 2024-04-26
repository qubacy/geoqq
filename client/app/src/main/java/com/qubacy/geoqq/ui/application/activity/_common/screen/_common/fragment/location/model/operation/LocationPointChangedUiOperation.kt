package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.yandex.mapkit.geometry.Point

class LocationPointChangedUiOperation(
    val locationPoint: Point
) : UiOperation {

}