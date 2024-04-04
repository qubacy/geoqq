package com.qubacy.geoqq.data.image.repository._common

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import com.qubacy.geoqq.data.image._common.extension.ImageExtension
import com.qubacy.geoqq.data.image._common.util.base64.extension.base64ToBitmap
import com.qubacy.geoqq.data.image._common.util.bitmap.extension.toBase64
import com.qubacy.geoqq.data.image.repository.source.http.request.UploadImageRequestImage
import com.qubacy.geoqq.data.image.repository.source.http.response.GetImageResponse

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

fun GetImageResponse.toRawImage(): RawImage {
    val extension = ImageExtension.getFormatById(extension)
    val bitmap = base64Content.base64ToBitmap()

    return RawImage(id, extension, bitmap)
}

fun RawImage.toUploadImageRequest(): UploadImageRequestImage {
    val extension = ImageExtension.getIdByFormat(extension)

    return UploadImageRequestImage(extension, content.toBase64(this.extension))
}