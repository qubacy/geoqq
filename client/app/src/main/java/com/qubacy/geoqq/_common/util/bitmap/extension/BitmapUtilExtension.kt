package com.qubacy.geoqq._common.util.bitmap.extension

import android.graphics.Bitmap
import java.nio.ByteBuffer

fun Bitmap.toByteArray(): ByteArray {
    val size = rowBytes * height
    val byteBuffer = ByteBuffer.allocate(size)

    copyPixelsToBuffer(byteBuffer)

    return byteBuffer.array()
}