package com.qubacy.geoqq._common._test._common.util.mock

import android.util.Base64
import org.mockito.Mockito
import kotlin.io.encoding.ExperimentalEncodingApi

object Base64MockUtil {
    @Volatile
    private var mIsMocked: Boolean = false

    @OptIn(ExperimentalEncodingApi::class)
    fun mockBase64() {
        if (mIsMocked) return

        Mockito.mockStatic(Base64::class.java)

        Mockito.`when`<ByteArray>(Base64.decode(
            Mockito.anyString(), Mockito.anyInt()
        )).thenAnswer {
            val encodedString = it.arguments[0] as String

            kotlin.io.encoding.Base64.decode(encodedString)
        }
        Mockito.`when`(Base64.encode(
            AnyMockUtil.anyObject(), Mockito.anyInt()
        )).thenAnswer {
            val bytesToDecode = it.arguments[0] as ByteArray

            kotlin.io.encoding.Base64.encodeToByteArray(bytesToDecode)
        }

        mIsMocked = true
    }
}