package com.qubacy.geoqq.common

import android.util.Base64
import org.mockito.Mockito

object Base64MockContext {
    fun mockBase64() {
        val mock = Mockito.mockStatic(Base64::class.java)

        Mockito.`when`(Base64.decode(Mockito.anyString(), Mockito.anyInt())).thenAnswer {
            java.util.Base64.getDecoder().decode(it.arguments[0] as String)
        }
        Mockito.`when`(Base64.encodeToString(Mockito.any(ByteArray::class.java), Mockito.anyInt()))
            .thenAnswer {
                String(java.util.Base64.getEncoder().encode(it.arguments[0] as ByteArray))
            }
    }
}