package com.qubacy.geoqq.common

import android.net.Uri
import org.mockito.Mockito

object UriMockContext {
    val mockedUri = Mockito.mock(Uri::class.java)

    fun mockUri() {
        val mock = Mockito.mockStatic(Uri::class.java)

        Mockito.`when`(Uri.parse(Mockito.anyString())).thenReturn(mockedUri)
    }
}