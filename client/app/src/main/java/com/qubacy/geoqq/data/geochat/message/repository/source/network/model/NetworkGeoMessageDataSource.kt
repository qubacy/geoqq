package com.qubacy.geoqq.data.geochat.message.repository.source.network.model

import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.MessageListResponse
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkGeoMessageDataSource : DataSource {
    @GET("/api/geo/chat/message")
    fun getGeoMessages(
        @Query("radius") radius: Int,
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("accessToken") accessToken: String
    ): Call<MessageListResponse>
}