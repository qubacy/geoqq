package com.qubacy.geoqq.data.mate.request.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.mate.request.repository.source.http.request.PostMateRequestRequest
import com.qubacy.geoqq.data.mate.request.repository.source.http.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.response.GetMateRequestsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HttpMateRequestDataSource : DataSource {
    @GET("/api/mate/request")
    fun getMateRequests(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("accessToken") accessToken: String
    ): Call<GetMateRequestsResponse>

    @GET("/api/mate/request/count")
    fun getMateRequestCount(
        @Query("accessToken") accessToken: String
    ): Call<GetMateRequestCountResponse>

    @POST("/api/mate/request")
    fun postMateRequest(
        @Body body: PostMateRequestRequest
    ): Call<Unit>

    @FormUrlEncoded
    @PUT("/api/mate/request/{id}")
    fun answerMateRequest(
        @Path("id") id: Long,
        @Field("access-token") accessToken: String,
        @Field("accepted") accepted: Boolean
    ): Call<Unit>
}