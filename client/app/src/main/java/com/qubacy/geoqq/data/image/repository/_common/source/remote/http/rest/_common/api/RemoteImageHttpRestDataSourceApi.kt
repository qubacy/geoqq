package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api

import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.request.GetImagesRequest
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.request.UploadImageRequest
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.GetImageResponse
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.GetImagesResponse
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.response.UploadImageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RemoteImageHttpRestDataSourceApi {
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