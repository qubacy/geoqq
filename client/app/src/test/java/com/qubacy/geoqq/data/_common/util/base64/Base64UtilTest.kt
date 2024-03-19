package com.qubacy.geoqq.data._common.util.base64

import org.junit.Assert
import org.junit.Test

class Base64UtilTest {
    companion object {
        const val STRING = "test"
        const val STRING_BASE64 = "dGVzdA=="
    }

    @Test
    fun bytesToStringTest() {
        val expectedString = STRING_BASE64
        val bytes = STRING.toByteArray()

        val gottenString = Base64Util.bytesToString(bytes)

        Assert.assertEquals(expectedString, gottenString)
    }

    @Test
    fun stringToBytesTest() {
        val expectedBytes = STRING.toByteArray()
        val encodedString = STRING_BASE64

        val gottenBytes = Base64Util.stringToBytes(encodedString)

        Assert.assertArrayEquals(expectedBytes, gottenBytes)
    }
}