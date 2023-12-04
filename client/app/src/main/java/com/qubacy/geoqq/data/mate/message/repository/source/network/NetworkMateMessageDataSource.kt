package com.qubacy.geoqq.data.mate.message.repository.source.network

import com.qubacy.geoqq.data.common.repository.message.source.network.model.response.MessageListResponse
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkMateMessageDataSource : DataSource {
    @GET("/api/mate/chat/{chatId}/message")
    fun getMateMessages(
        @Path("chatId") chatId: Long,
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("accessToken") accessToken: String
    ): Call<MessageListResponse>
}