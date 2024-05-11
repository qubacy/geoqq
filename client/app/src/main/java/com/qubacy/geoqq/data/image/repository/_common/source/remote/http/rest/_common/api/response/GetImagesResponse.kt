package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImagesResponse(
    val images: List<GetImageResponse>
) {

}