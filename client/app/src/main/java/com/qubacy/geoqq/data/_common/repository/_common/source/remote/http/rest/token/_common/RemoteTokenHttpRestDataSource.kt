package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.api.response.UpdateTokensResponse

interface RemoteTokenHttpRestDataSource {
    fun updateTokens(refreshToken: String): UpdateTokensResponse
}