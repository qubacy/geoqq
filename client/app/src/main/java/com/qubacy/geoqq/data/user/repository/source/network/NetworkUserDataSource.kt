package com.qubacy.geoqq.data.user.repository.source.network

import com.qubacy.geoqq.data.user.repository.source.network.model.request.GetUsersRequestBody
import com.qubacy.geoqq.data.user.repository.source.network.model.response.GetUsersResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import javax.sql.DataSource

interface NetworkUserDataSource : DataSource {
    @POST("/api/user")
    fun getUsers(
        @Body getUsersRequestBody: GetUsersRequestBody
    ): Call<GetUsersResponse>
}