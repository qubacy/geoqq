package com.qubacy.geoqq.ui._common._test.view.util.assertion.mapview.changed

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.qubacy.geoqq._common.point._test.util.PointUtils
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.mapview.MapView

class MapViewChangedViewAssertion(
    val prevVisibleRegion: VisibleRegion
) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException

        view as MapView

        val visibleRegion = view.mapWindow.map.visibleRegion

        PointUtils.assertNotEqualPoints(prevVisibleRegion.topLeft, visibleRegion.topLeft)
        PointUtils.assertNotEqualPoints(prevVisibleRegion.topRight, visibleRegion.topRight)
        PointUtils.assertNotEqualPoints(prevVisibleRegion.bottomRight, visibleRegion.bottomRight)
        PointUtils.assertNotEqualPoints(prevVisibleRegion.bottomLeft, visibleRegion.bottomLeft)
    }
}