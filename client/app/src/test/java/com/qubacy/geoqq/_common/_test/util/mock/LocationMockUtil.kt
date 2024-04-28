package com.qubacy.geoqq._common._test.util.mock

import android.location.Location
import org.mockito.Mockito

object LocationMockUtil {
    fun mockLocation(latitude: Double, longitude: Double): Location {
        val locationMock = Mockito.mock(Location::class.java)

        Mockito.`when`(locationMock.latitude).thenAnswer { latitude }
        Mockito.`when`(locationMock.longitude).thenAnswer { longitude }

        return locationMock
    }
}