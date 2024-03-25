package com.qubacy.geoqq.data.image.repository._common

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat

data class RawImage(
    val id: Long? = null,
    val extension: CompressFormat,
    val content: Bitmap
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other::class != RawImage::class) return false

        other as RawImage

        return (id == other.id && extension == other.extension && content.sameAs(other.content))
    }
}