package com.qubacy.geoqq.domain._common.model.image

import android.net.Uri
import com.qubacy.geoqq.data.image.model.DataImage

data class Image(
    val id: Long,
    val uri: Uri
) {

}

fun DataImage.toImage(): Image {
    return Image(id, uri)
}