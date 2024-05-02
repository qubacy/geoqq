package com.qubacy.geoqq.data.mate.request.repository.source.http.api

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.request.PostMateRequestRequest
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HttpMateRequestDataSourceApi : DataSource {
    @GET("/api/mate/request")
    fun getMateRequests(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Call<GetMateRequestsResponse>

    @GET("/api/mate/request/count")
    fun getMateRequestCount(): Call<GetMateRequestCountResponse>

    @POST("/api/mate/request")
    fun postMateRequest(
        @Body body: PostMateRequestRequest
    ): Call<Unit>

    @FormUrlEncoded
    @PUT("/api/mate/request/{id}")
    fun answerMateRequest(
        @Path("id") id: Long,
        @Field("accepted") accepted: Boolean
    ): Call<Unit>
}