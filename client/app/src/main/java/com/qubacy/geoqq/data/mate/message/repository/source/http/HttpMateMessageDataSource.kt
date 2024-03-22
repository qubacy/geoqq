package com.qubacy.geoqq.data.mate.message.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message.GetMessagesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HttpMateMessageDataSource : DataSource {
    @GET("/api/mate/chat/{chatId}/message")
    fun getMateMessages(
        @Path("chatId") chatId: Long,
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("accessToken") accessToken: String
    ): Call<GetMessagesResponse>
}