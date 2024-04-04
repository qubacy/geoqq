package com.qubacy.geoqq._common.util.bitmap.extension

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(compressFormat: CompressFormat): ByteArray {
    val byteArrayStream = ByteArrayOutputStream()

    compress(compressFormat, 100, byteArrayStream)

    return byteArrayStream.toByteArray()
}