package com.qubacy.geoqq.data.image.repository.source.http.api

import com.qubacy.geoqq.data.image.repository.source.http.api.request.GetImagesRequest
import com.qubacy.geoqq.data.image.repository.source.http.api.request.UploadImageRequest
import com.qubacy.geoqq.data.image.repository.source.http.api.response.GetImageResponse
import com.qubacy.geoqq.data.image.repository.source.http.api.response.GetImagesResponse
import com.qubacy.geoqq.data.image.repository.source.http.api.response.UploadImageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HttpImageDataSourceApi {
    @GET("/api/image/{id}")
    fun getImage(
        @Path("id") id: Long
    ): Call<GetImageResponse>

    @POST("/api/image")
    fun getImages(
        @Body getImagesRequestBody: GetImagesRequest
    ): Call<GetImagesResponse>

    @POST("/api/image/new")
    fun uploadImage(
        @Body uploadImageRequestBody: UploadImageRequest
    ): Call<UploadImageResponse>
}