package com.qubacy.geoqq.data._common.util.base64

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Base64Util {
    const val TAG = "Base64Util"

    @OptIn(ExperimentalEncodingApi::class)
    fun bytesToString(bytes: ByteArray): String {
        return Base64.encode(bytes)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun stringToBytes(base64String: String): ByteArray {
        return Base64.decode(base64String)
    }
}