package com.qubacy.geoqq.data.mate.message.repository.source.network

import com.qubacy.geoqq.data.common.message.repository.source.network.model.request.SendMessageRequestBody
import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.MessageListResponse
import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.SendMessageResponse
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkMateMessageDataSource : DataSource {
    @GET("/api/mate/chat/{chatId}/message")
    fun getMateMessage(
        @Path("chatId") chatId: Long,
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("accessToken") accessToken: String
    ): Call<MessageListResponse>

    @POST("/api/mate/chat/{chatId}/message")
    fun sendMateMessage(
        @Path("chatId") chatId: Long,
        @Body() sendMateMessageBody: SendMessageRequestBody
    ): Call<SendMessageResponse>
}