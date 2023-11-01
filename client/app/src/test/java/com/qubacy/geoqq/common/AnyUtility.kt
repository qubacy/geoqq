package com.qubacy.geoqq.common

import org.mockito.Mockito

object AnyUtility {
    fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}