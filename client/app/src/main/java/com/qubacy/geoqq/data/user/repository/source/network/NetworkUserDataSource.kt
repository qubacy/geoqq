package com.qubacy.geoqq.data.user.repository.source.network

import com.qubacy.geoqq.data.user.repository.source.network.response.GetUserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.sql.DataSource

interface NetworkUserDataSource : DataSource {
    @GET("/api/user/{userId}")
    fun getUser(
        @Path("userId", encoded = true) userId: Long,
        @Query("accessToken") accessToken: String
    ): Call<GetUserResponse>
}