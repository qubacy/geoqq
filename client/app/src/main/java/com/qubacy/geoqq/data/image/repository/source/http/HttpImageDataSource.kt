package com.qubacy.geoqq.data.image.repository.source.http

import com.qubacy.geoqq.data.image.repository.source.http.request.GetImagesRequest
import com.qubacy.geoqq.data.image.repository.source.http.request.UploadImageRequest
import com.qubacy.geoqq.data.image.repository.source.http.response.GetImageResponse
import com.qubacy.geoqq.data.image.repository.source.http.response.GetImagesResponse
import com.qubacy.geoqq.data.image.repository.source.http.response.UploadImageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HttpImageDataSource {
    @GET("/api/image/{id}")
    fun getImage(
        @Path("id") id: Long,
        @Query("access-token") accessToken: String
    ): Call<GetImageResponse>

    @GET("/api/image")
    fun getImages(
        @Body getImagesRequestBody: GetImagesRequest
    ): Call<GetImagesResponse>

    @POST("/api/image")
    fun uploadImage(
        @Body uploadImageRequestBody: UploadImageRequest
    ): Call<UploadImageResponse>
}