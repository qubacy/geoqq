package com.qubacy.geoqq.data.image.model

import android.net.Uri
import com.qubacy.geoqq.data.image.repository.source.local.entity.ImageEntity

data class DataImage(
    val id: Long,
    val uri: Uri
) {

}

fun ImageEntity.toDataImage(): DataImage {
    return DataImage(id, uri)
}