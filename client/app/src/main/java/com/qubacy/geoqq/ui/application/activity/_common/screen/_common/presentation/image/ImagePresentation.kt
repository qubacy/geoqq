package com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image

import android.net.Uri
import com.qubacy.geoqq.domain._common.model.image.Image

data class ImagePresentation(
    val id: Long,
    val uri: Uri
) {

}

fun Image.toImagePresentation(): ImagePresentation {
    return ImagePresentation(id, uri)
}