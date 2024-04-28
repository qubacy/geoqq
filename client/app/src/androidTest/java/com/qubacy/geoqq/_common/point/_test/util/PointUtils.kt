package com.qubacy.geoqq._common.point._test.util

import com.yandex.mapkit.geometry.Point
import org.junit.Assert

object PointUtils {
    fun assertEqualPoints(expected: Point, gotten: Point) {
        Assert.assertEquals(expected.latitude, gotten.latitude, 0.0)
        Assert.assertEquals(expected.longitude, gotten.longitude, 0.0)
    }

    fun assertNotEqualPoints(expected: Point, gotten: Point) {
        Assert.assertNotEquals(expected.latitude, gotten.latitude, 0.0)
        Assert.assertNotEquals(expected.longitude, gotten.longitude, 0.0)
    }
}