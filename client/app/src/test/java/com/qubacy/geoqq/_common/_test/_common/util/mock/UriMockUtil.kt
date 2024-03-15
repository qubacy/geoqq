package com.qubacy.geoqq._common._test._common.util.mock

import android.net.Uri
import org.mockito.Mockito

object UriMockUtil {
    private fun mockUri() {
        Mockito.mockStatic(Uri::class.java)

        val uri: Uri = Mockito.mock(Uri::class.java)

        Mockito.`when`<Uri>(Uri.parse(Mockito.anyString())).thenReturn(uri)
    }

    fun getMockedUri(): Uri {
        return try {
            Uri.parse(String())

        } catch (e: Exception) {
            mockUri()
            Uri.parse(String())
        }
    }
}