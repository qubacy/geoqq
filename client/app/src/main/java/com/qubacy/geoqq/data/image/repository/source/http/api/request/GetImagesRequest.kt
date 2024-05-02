package com.qubacy.geoqq.data.image.repository.source.http.api.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImagesRequest(
    val ids: List<Long>
) {

}