package com.qubacy.geoqq.common.util.mock

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.mockito.Mockito

object BitmapMockContext {
    fun mockBitmapFactory() {
        Mockito.mockStatic(BitmapFactory::class.java)

        val bitmapMock = Mockito.mock(Bitmap::class.java)

        Mockito.`when`(BitmapFactory.decodeByteArray(
            Mockito.any(ByteArray::class.java), Mockito.anyInt(), Mockito.anyInt()
        )).thenReturn(bitmapMock)
    }
}