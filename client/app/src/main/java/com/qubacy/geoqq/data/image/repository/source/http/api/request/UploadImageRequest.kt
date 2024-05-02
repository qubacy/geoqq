package com.qubacy.geoqq.data.image.repository.source.http.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UploadImageRequest(
    val image: UploadImageRequestImage
) {

}

@JsonClass(generateAdapter = true)
class UploadImageRequestImage(
    @Json(name = "ext") val extension: Int,
    @Json(name = "content") val base64Content: String
) {

}