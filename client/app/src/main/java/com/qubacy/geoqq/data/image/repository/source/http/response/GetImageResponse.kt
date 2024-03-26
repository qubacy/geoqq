package com.qubacy.geoqq.data.image.repository.source.http.response

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