package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.api.RemoteTokenHttpRestDataSourceApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.api.response.UpdateTokensResponse
import javax.inject.Inject

class RemoteTokenHttpRestDataSource @Inject constructor(
    private val mHttpCallExecutor: HttpCallExecutor
) : DataSource {
    private lateinit var mRemoteTokenHttpRestDataSourceApi: RemoteTokenHttpRestDataSourceApi

    fun setHttpTokenDataSourceApi(remoteTokenHttpRestDataSourceApi: RemoteTokenHttpRestDataSourceApi) {
        mRemoteTokenHttpRestDataSourceApi = remoteTokenHttpRestDataSourceApi
    }

    fun updateTokens(refreshToken: String): UpdateTokensResponse {
        val updateTokensCall = mRemoteTokenHttpRestDataSourceApi.updateTokens(refreshToken)
        val updateTokensResponse = mHttpCallExecutor.executeNetworkRequest(updateTokensCall)

        return updateTokensResponse
    }
}