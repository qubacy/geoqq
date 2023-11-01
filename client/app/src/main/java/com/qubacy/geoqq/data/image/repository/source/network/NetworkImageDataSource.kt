package com.qubacy.geoqq.data.image.repository.source.network

import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkImageDataSource {
    @GET("/api/resources/image/{imageId}")
    fun getImage(@Path("imageId", encoded = true) imageId: Long): Call<GetImageResult>
}