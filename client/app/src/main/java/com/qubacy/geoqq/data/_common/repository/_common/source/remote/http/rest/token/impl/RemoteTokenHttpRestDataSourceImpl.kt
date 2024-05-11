package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.api.RemoteTokenHttpRestDataSourceApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.api.response.UpdateTokensResponse
import javax.inject.Inject

class RemoteTokenHttpRestDataSourceImpl @Inject constructor(
    private val mHttpCallExecutor: HttpCallExecutor
) : RemoteTokenHttpRestDataSource {
    private lateinit var mRemoteTokenHttpRestDataSourceApi: RemoteTokenHttpRestDataSourceApi

    fun setHttpTokenDataSourceApi(remoteTokenHttpRestDataSourceApi: RemoteTokenHttpRestDataSourceApi) {
        mRemoteTokenHttpRestDataSourceApi = remoteTokenHttpRestDataSourceApi
    }

    override fun updateTokens(refreshToken: String): UpdateTokensResponse {
        val updateTokensCall = mRemoteTokenHttpRestDataSourceApi.updateTokens(refreshToken)
        val updateTokensResponse = mHttpCallExecutor.executeNetworkRequest(updateTokensCall)

        return updateTokensResponse
    }
}