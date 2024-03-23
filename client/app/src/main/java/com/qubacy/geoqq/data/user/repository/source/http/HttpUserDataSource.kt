package com.qubacy.geoqq.data.user.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.user.repository.source.http.request.GetUsersRequest
import com.qubacy.geoqq.data.user.repository.source.http.response.GetUsersResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HttpUserDataSource : DataSource {
    @POST("/api/user")
    fun getUsers(
        @Body getUsersRequestBody: GetUsersRequest
    ): Call<GetUsersResponse>
}