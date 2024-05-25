package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.RemoteUserHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.RemoteUserHttpRestDataSourceApi
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.request.GetUsersRequest
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUsersResponse
import javax.inject.Inject

class RemoteUserHttpRestDataSourceImpl @Inject constructor(
    private val mRemoteUserDataHttpRestSourceApi: RemoteUserHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutorImpl
) : RemoteUserHttpRestDataSource {
    override fun getUsers(ids: List<Long>): GetUsersResponse {
        val getUsersRequest = GetUsersRequest(ids)
        val getUsersCall = mRemoteUserDataHttpRestSourceApi.getUsers(getUsersRequest)
        val getUsersResponse = mHttpCallExecutor.executeNetworkRequest(getUsersCall)

        return getUsersResponse
    }
}