package com.qubacy.geoqq.data.mate.chat.repository.source.http.api

import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.response.GetChatsResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HttpMateChatDataSourceApi {
    @GET("/api/mate/chat")
    fun getChats(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Call<GetChatsResponse>

    @GET("/api/mate/chat/{id}")
    fun getChat(
        @Path("id") id: Long,
    ): Call<GetChatResponse>

    @DELETE("/api/mate/chat/{id}")
    fun deleteChat(
        @Path("id") id: Long
    ): Call<Unit>
}