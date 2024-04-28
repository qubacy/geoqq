package com.qubacy.geoqq.ui._common._test.view.util.assertion.mapview.loaded

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.yandex.mapkit.mapview.MapView
import org.junit.Assert

class MapViewLoadedViewAssertion : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException

        view as MapView

        Assert.assertTrue(view.mapWindow.map.isValid) // todo: not sure about this;
    }
}