package com.qubacy.geoqq.data.image._common.util.base64.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.qubacy.geoqq.data._common.util.base64.Base64Util

fun String.base64ToBitmap(): Bitmap {
    val bitmapBytes = Base64Util.stringToBytes(this)

    return BitmapFactory.decodeByteArray(bitmapBytes, 0, length)
}