package com.qubacy.geoqq.data.mate.chat.repository.source.http

import com.qubacy.geoqq.data.mate.chat.repository.source.http.request.DeleteChatRequest
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Path
import retrofit2.http.Query

interface HttpMateChatDataSource {
    @GET("/api/mate/chat")
    fun getChats(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("accessToken") accessToken: String
    ): Call<GetChatsResponse>

    @GET("/api/mate/chat/{id}")
    fun getChat(
        @Path("id") id: Long,
        @Query("accessToken") accessToken: String
    ): Call<GetChatResponse>

    @HTTP(method = "DELETE", path = "/api/mate/chat/{id}", hasBody = true)
    fun deleteChat(
        @Path("id") id: Long,
        @Body body: DeleteChatRequest
    ): Call<Unit>
}