package com.qubacy.geoqq.data.user.repository.source.http.api

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.user.repository.source.http.api.request.GetUsersRequest
import com.qubacy.geoqq.data.user.repository.source.http.api.response.GetUsersResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HttpUserDataSourceApi : DataSource {
    @POST("/api/user")
    fun getUsers(
        @Body getUsersRequestBody: GetUsersRequest
    ): Call<GetUsersResponse>
}