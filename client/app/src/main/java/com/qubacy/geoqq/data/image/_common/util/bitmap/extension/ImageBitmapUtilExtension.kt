package com.qubacy.geoqq.data.image._common.util.bitmap.extension

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import com.qubacy.geoqq._common.util.bitmap.extension.toByteArray
import com.qubacy.geoqq.data._common.util.base64.Base64Util

fun Bitmap.toBase64(compressFormat: CompressFormat): String {
    val bitmapBytes = this.toByteArray(compressFormat)

    return Base64Util.bytesToString(bitmapBytes)
}