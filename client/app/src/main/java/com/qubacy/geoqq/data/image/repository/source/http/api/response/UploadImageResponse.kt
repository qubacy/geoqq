package com.qubacy.geoqq.data.image.repository.source.http.api.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UploadImageResponse(
    val id: Long
) {

}