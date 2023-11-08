package com.qubacy.geoqq.common

import android.net.Uri
import org.mockito.Mockito

object UriMockContext {
    fun mockUri() {
        Mockito.mockStatic(Uri::class.java)

        val uri: Uri = Mockito.mock(Uri::class.java)

        Mockito.`when`<Uri>(Uri.parse(Mockito.anyString())).thenReturn(uri)
    }
}