package com.qubacy.geoqq.data.image.repository.source.http.request

import com.qubacy.geoqq.data.image._common.extension.ImageExtension
import com.qubacy.geoqq.data.image._common.util.bitmap.extension.toBase64
import com.qubacy.geoqq.data.image.repository._common.RawImage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UploadImageRequest(
    @Json(name = "access-token") val accessToken: String,
    val image: UploadImageRequestImage
) {

}

@JsonClass(generateAdapter = true)
class UploadImageRequestImage(
    @Json(name = "ext") val extension: String,
    @Json(name = "content") val base64Content: String
) {
    companion object {
        fun create(rawImage: RawImage): UploadImageRequestImage {
            val extension = ImageExtension.getStringByFormat(rawImage.extension)
            val content = rawImage.content.toBase64()

            return UploadImageRequestImage(extension, content)
        }
    }
}