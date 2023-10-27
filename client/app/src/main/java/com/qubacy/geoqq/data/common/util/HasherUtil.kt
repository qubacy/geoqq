package com.qubacy.geoqq.data.common.util

import java.security.MessageDigest

object HasherUtil {
    enum class HashAlgorithm(val id: Int, val title: String) {
        SHA256(0, "SHA-256");
    }

    fun hashString(
        string: String,
        hashAlgorithm: HashAlgorithm
    ): String {
        val messageDigest = MessageDigest.getInstance(hashAlgorithm.title)
        val stringBytes = string.encodeToByteArray()

        return String(messageDigest.digest(stringBytes))
    }
}