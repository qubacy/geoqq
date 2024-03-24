package com.qubacy.geoqq.data.image.repository.source.http.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetImagesResponse(
    val images: List<GetImageResponse>
) {

}