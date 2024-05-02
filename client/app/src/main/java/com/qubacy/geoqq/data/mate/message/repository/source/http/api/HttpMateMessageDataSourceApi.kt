package com.qubacy.geoqq.data.mate.message.repository.source.http.api

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.mate.message.repository.source.http.api.request.SendMateMessageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HttpMateMessageDataSourceApi : DataSource {
    @GET("/api/mate/chat/{chatId}/message")
    fun getMateMessages(
        @Path("chatId") chatId: Long,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Call<GetMessagesResponse>

    // todo: delete:
    @POST("/api/mate/chat/{chatId}/message")
    fun sendMateMessage(
        @Path("chatId") chatId: Long,
        @Body body: SendMateMessageRequest
    ): Call<Unit>
}