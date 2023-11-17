package com.qubacy.geoqq.data.image.repository.source.network

import com.qubacy.geoqq.data.image.repository.source.network.model.request.GetImagesRequestBody
import com.qubacy.geoqq.data.image.repository.source.network.model.response.GetImagesResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkImageDataSource {
    @POST("/api/image")
    fun getImages(
        @Body getImagesRequestBody: GetImagesRequestBody
    ): Call<GetImagesResponse>
}