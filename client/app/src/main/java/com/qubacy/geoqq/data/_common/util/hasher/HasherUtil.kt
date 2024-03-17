package com.qubacy.geoqq.data._common.util.hasher

import java.security.MessageDigest

object HasherUtil {
    enum class HashAlgorithm(val id: Int, val title: String) {
        SHA256(0, "SHA-256");
    }

    fun hashString(
        string: String,
        hashAlgorithm: HashAlgorithm
    ): ByteArray {
        val messageDigest = MessageDigest.getInstance(hashAlgorithm.title)
        val stringBytes = string.encodeToByteArray()

        return messageDigest.digest(stringBytes)
    }
}