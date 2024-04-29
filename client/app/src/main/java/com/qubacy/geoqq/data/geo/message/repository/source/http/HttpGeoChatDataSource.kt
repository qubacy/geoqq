package com.qubacy.geoqq.data.geo.message.repository.source.http

import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HttpGeoChatDataSource {
    @GET("/api/geo/chat/all")
    fun getMessages(
        @Query("accessToken") accessToken: String,
        @Query("radius") radius: Int,
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double
    ): Call<GetMessagesResponse>
}