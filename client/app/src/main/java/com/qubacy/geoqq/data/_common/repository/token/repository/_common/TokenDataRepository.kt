package com.qubacy.geoqq.data._common.repository.token.repository._common

import com.qubacy.geoqq.data._common.repository.token.repository._common.result.get.GetTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.update.UpdateTokensDataResult

interface TokenDataRepository {
    suspend fun getTokens(): GetTokensDataResult
    suspend fun updateTokens(): UpdateTokensDataResult
    suspend fun saveTokens(
        refreshToken: String,
        accessToken: String
    )
    suspend fun reset()
}