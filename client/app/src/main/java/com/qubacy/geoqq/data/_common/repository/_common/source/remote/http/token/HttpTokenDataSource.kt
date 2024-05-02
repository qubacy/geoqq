package com.qubacy.geoqq.data._common.repository._common.source.remote.http.token

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.HttpTokenDataSourceApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.response.UpdateTokensResponse
import javax.inject.Inject

class HttpTokenDataSource @Inject constructor(
    private val mHttpCallExecutor: HttpCallExecutor
) : DataSource {
    private lateinit var mHttpTokenDataSourceApi: HttpTokenDataSourceApi

    fun setHttpTokenDataSourceApi(httpTokenDataSourceApi: HttpTokenDataSourceApi) {
        mHttpTokenDataSourceApi = httpTokenDataSourceApi
    }

    fun updateTokens(refreshToken: String): UpdateTokensResponse {
        val updateTokensCall = mHttpTokenDataSourceApi.updateTokens(refreshToken)
        val updateTokensResponse = mHttpCallExecutor.executeNetworkRequest(updateTokensCall)

        return updateTokensResponse
    }
}