package com.qubacy.geoqq._common._test.util.mock

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.mockito.Mockito

object BitmapFactoryMockUtil {
    @Volatile
    private var mIsMocked: Boolean = false

    fun mockBitmapFactory() {
        if (mIsMocked) return

        Mockito.mockStatic(BitmapFactory::class.java)

        val bitmapMock = Mockito.mock(Bitmap::class.java)

        Mockito.`when`<Bitmap>(BitmapFactory.decodeByteArray(
            AnyMockUtil.anyObject(), Mockito.anyInt(), Mockito.anyInt()
        )).thenReturn(bitmapMock)

        mIsMocked = true
    }
}