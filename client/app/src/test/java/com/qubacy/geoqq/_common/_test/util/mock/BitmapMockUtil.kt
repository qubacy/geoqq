package com.qubacy.geoqq._common._test.util.mock

import android.graphics.Bitmap
import org.mockito.Mockito

object BitmapMockUtil {
    fun mockBitmap(): Bitmap {
        val bitmapMock = Mockito.mock(Bitmap::class.java)

        Mockito.`when`(bitmapMock.compress(
            AnyMockUtil.anyObject(), Mockito.anyInt(), AnyMockUtil.anyObject()
        )).thenAnswer { true }

        return bitmapMock
    }
}