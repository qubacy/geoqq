package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImagesRequest(
    val ids: List<Long>
) {

}