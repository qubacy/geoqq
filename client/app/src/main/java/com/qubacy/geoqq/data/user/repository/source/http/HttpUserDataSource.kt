package com.qubacy.geoqq.data.user.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.user.repository.source.http.api.HttpUserDataSourceApi
import com.qubacy.geoqq.data.user.repository.source.http.api.request.GetUsersRequest
import com.qubacy.geoqq.data.user.repository.source.http.api.response.GetUsersResponse
import javax.inject.Inject

class HttpUserDataSource @Inject constructor(
    private val mHttpUserDataSourceApi: HttpUserDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    fun getUsers(ids: List<Long>): GetUsersResponse {
        val getUsersRequest = GetUsersRequest(ids)
        val getUsersCall = mHttpUserDataSourceApi.getUsers(getUsersRequest)
        val getUsersResponse = mHttpCallExecutor.executeNetworkRequest(getUsersCall)

        return getUsersResponse
    }
}