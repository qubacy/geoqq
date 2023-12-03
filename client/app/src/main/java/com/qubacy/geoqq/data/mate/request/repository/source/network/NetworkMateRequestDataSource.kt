package com.qubacy.geoqq.data.mate.request.repository.source.network

import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.AnswerMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.CreateMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.GetMateRequestsResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkMateRequestDataSource : DataSource {
    @GET("/api/mate/request/count")
    fun getMateRequestCount(
        @Query("accessToken") accessToken: String
    ): Call<GetMateRequestCountResponse>

    @GET("/api/mate/request")
    fun getMateRequests(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("accessToken") accessToken: String
    ): Call<GetMateRequestsResponse>

    @FormUrlEncoded
    @POST("/api/mate/request")
    fun createMateRequest(
        @Field("access-token") accessToken: String,
        @Field("user-id") userId: Long
    ): Call<CreateMateRequestResponse>

    @FormUrlEncoded
    @PUT("/api/mate/request/{id}")
    fun answerMateRequest(
        @Path("id") id: Long,
        @Field("access-token") accessToken: String,
        @Field("accepted") accepted: Boolean
    ): Call<AnswerMateRequestResponse>
}