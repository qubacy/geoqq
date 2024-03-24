package com.qubacy.geoqq.data.image.repository.source.http.response

import com.qubacy.geoqq.data.image._common.extension.ImageExtension
import com.qubacy.geoqq.data.image._common.util.base64.extension.base64ToBitmap
import com.qubacy.geoqq.data.image.repository._common.RawImage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImageResponse(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = EXTENSION_PROP_NAME) val extension: String,
    @Json(name = CONTENT_PROP_NAME) val base64Content: String
) {
    companion object {
        const val ID_PROP_NAME = "id"
        const val EXTENSION_PROP_NAME = "ext"
        const val CONTENT_PROP_NAME = "content"
    }
}

fun GetImageResponse.toRawImage(): RawImage {
    val extension = ImageExtension.getFormatByString(extension)
    val bitmap = base64Content.base64ToBitmap()

    return RawImage(id, extension, bitmap)
}