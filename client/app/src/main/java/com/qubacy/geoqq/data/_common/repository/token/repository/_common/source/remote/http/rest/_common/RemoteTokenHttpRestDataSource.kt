package com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api.response.UpdateTokensResponse

interface RemoteTokenHttpRestDataSource {
    fun updateTokens(refreshToken: String): UpdateTokensResponse
}