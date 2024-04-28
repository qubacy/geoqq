package com.qubacy.geoqq._common.point._test.util

import com.yandex.mapkit.geometry.Point
import org.junit.Assert

object PointUtils {
    fun assertPoints(expected: Point, gotten: Point) {
        Assert.assertEquals(expected.latitude, gotten.latitude, 0.0)
        Assert.assertEquals(expected.longitude, gotten.longitude, 0.0)
    }
}