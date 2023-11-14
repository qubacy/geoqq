package com.qubacy.geoqq.domain.common.usecase.util.extension.token

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult

interface TokenExtension {
    suspend fun getAccessTokenExtension(
        tokenDataRepository: TokenDataRepository
    ): Result {
        val getTokensResult = tokenDataRepository.getTokens()

        if (getTokensResult is ErrorResult) return getTokensResult

        val getTokensResultCast = getTokensResult as GetTokensResult

        return GetAccessTokenResult(getTokensResultCast.accessToken)
    }
}