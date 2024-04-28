package com.qubacy.geoqq.ui._common._test.view.util.action.pinch

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

object PinchZoomViewAction {
    const val DEFAULT_STEP_COUNT = 100

    fun perform(
        uiSelector: UiSelector,
        percent: Int,
        stepCount: Int = DEFAULT_STEP_COUNT
    ) {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        uiDevice.findObject(uiSelector).also {
            if (percent >= 0) it.pinchIn(percent, stepCount)
            else it.pinchOut(percent, stepCount)
        }
    }
}