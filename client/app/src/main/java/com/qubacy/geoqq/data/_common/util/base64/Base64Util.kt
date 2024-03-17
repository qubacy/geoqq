package com.qubacy.geoqq.data._common.util.base64

import android.util.Base64

object Base64Util {
    private const val DEFAULT_FLAGS = Base64.DEFAULT

    fun bytesToString(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, DEFAULT_FLAGS)
    }

    fun stringToBytes(base64String: String): ByteArray {
        return Base64.decode(base64String, DEFAULT_FLAGS)
    }
}