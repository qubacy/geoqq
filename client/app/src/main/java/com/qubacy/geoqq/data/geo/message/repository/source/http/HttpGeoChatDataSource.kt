package com.qubacy.geoqq.data.geo.message.repository.source.http

import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.geo.message.repository.source.http.request.SendMessageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HttpGeoChatDataSource {
    @GET("/api/geo/chat/message/all")
    fun getMessages(
        @Query("accessToken") accessToken: String,
        @Query("radius") radius: Int,
        @Query("lon") longitude: Float,
        @Query("lat") latitude: Float
    ): Call<GetMessagesResponse>

    /**
     * Only for debug purposes:
     */
    @POST("/api/geo/chat/message")
    fun sendMessage(
        @Body body: SendMessageRequest
    ): Call<Unit>
}