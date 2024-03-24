package com.qubacy.geoqq.data.image.repository._common

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat

data class RawImage(
    val id: Long? = null,
    val extension: CompressFormat,
    val content: Bitmap
) {

}