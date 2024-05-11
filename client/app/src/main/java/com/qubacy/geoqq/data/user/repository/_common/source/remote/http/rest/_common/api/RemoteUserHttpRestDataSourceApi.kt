package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api

import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.request.GetUsersRequest
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUsersResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RemoteUserHttpRestDataSourceApi {
    @POST("/api/user")
    fun getUsers(
        @Body getUsersRequestBody: GetUsersRequest
    ): Call<GetUsersResponse>
}