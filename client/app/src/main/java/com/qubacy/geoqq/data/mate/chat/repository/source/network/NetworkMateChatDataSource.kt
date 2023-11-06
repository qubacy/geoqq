package com.qubacy.geoqq.data.mate.chat.repository.source.network

import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.response.GetChatsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkMateChatDataSource : DataSource {
    @GET("/api/mate/chat")
    fun getChats(@Query("accessToken") accessToken: String): Call<GetChatsResponse>
}