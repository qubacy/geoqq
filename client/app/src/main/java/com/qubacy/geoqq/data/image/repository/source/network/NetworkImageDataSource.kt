package com.qubacy.geoqq.data.image.repository.source.network

import com.qubacy.geoqq.data.image.repository.source.network.response.GetImageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkImageDataSource {
    @GET("/api/resources/image/{imageId}")
    fun getImage(
        @Path("imageId", encoded = true) imageId: Long,
        @Query("accessToken") accessToken: String
    ): Call<GetImageResponse>
}