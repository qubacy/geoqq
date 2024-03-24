package com.qubacy.geoqq.data.image.repository.source.local.entity

import android.net.Uri
import com.qubacy.geoqq.data.image.model.DataImage

data class ImageEntity(
    val id: Long,
    val uri: Uri
) {

}

fun ImageEntity.toDataImage(): DataImage {
    return DataImage(id, uri)
}