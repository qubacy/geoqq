package com.qubacy.geoqq.data.common.util

import android.util.Base64

object StringEncodingDecodingUtil {
    private const val DEFAULT_FLAGS = Base64.DEFAULT

    fun bytesAsBase64String(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, DEFAULT_FLAGS)
    }

    fun base64StringAsBytes(base64String: String): ByteArray {
        return Base64.decode(base64String, DEFAULT_FLAGS)
    }
}