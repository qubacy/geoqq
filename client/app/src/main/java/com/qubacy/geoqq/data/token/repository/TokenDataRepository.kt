package com.qubacy.geoqq.data.token.repository

import com.auth0.android.jwt.Claim
import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.token.repository.result.CheckAccessTokenValidityResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenExistenceResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenPayloadResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensWithNetworkResult
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.model.response.UpdateTokensResponse
import retrofit2.Call

class TokenDataRepository(
    val localTokenDataSource: LocalTokenDataSource,
    val networkTokenDataSource: NetworkTokenDataSource
) : NetworkDataRepository() {
    private fun checkLocalRefreshTokenExistence(): Result {
        val curRefreshToken = localTokenDataSource.loadRefreshToken()

        return CheckRefreshTokenExistenceResult(curRefreshToken != null)
    }

    private fun checkRefreshTokenValidity(): Result {
        val curRefreshToken = localTokenDataSource.loadRefreshToken()

        if (curRefreshToken == null)
            return ErrorResult(
                ErrorContext.Token.LOCAL_REFRESH_TOKEN_NOT_FOUND.id)

        val isCurRefreshTokenValid = localTokenDataSource
            .checkTokenForValidity(curRefreshToken)

        return CheckRefreshTokenValidityResult(isCurRefreshTokenValid)
    }

    private fun checkAccessTokenValidity(): Result {
        val curAccessToken = localTokenDataSource.accessToken

        if (curAccessToken == null)
            return CheckAccessTokenValidityResult(false)

        val isValid = localTokenDataSource.checkTokenForValidity(curAccessToken)

        return CheckAccessTokenValidityResult(isValid)
    }

    private fun getTokensWithNetwork(refreshToken: String): Result {
        val networkCall = networkTokenDataSource.updateTokens(refreshToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as UpdateTokensResponse

        val updatedRefreshToken = responseBody.refreshToken
        val updatedAccessToken = responseBody.accessToken

        return GetTokensWithNetworkResult(updatedRefreshToken, updatedAccessToken)
    }

    suspend fun getTokens(): Result {
        val checkRefreshTokenExistenceResult = checkLocalRefreshTokenExistence()

        if (checkRefreshTokenExistenceResult is ErrorResult) return checkRefreshTokenExistenceResult
        if (!(checkRefreshTokenExistenceResult as CheckRefreshTokenExistenceResult).isExisting)
            return ErrorResult(ErrorContext.Token.LOCAL_REFRESH_TOKEN_NOT_FOUND.id)

        val checkRefreshTokenValidityResult = checkRefreshTokenValidity()

        if (checkRefreshTokenValidityResult is ErrorResult) return checkRefreshTokenValidityResult
        if (!(checkRefreshTokenValidityResult as CheckRefreshTokenValidityResult).isValid)
            return ErrorResult(ErrorContext.Token.LOCAL_REFRESH_TOKEN_INVALID.id)

        val checkAccessTokenValidityResult = checkAccessTokenValidity()

        if (checkAccessTokenValidityResult is ErrorResult) return checkAccessTokenValidityResult
        if ((checkAccessTokenValidityResult as CheckAccessTokenValidityResult).isValid)
            return GetTokensResult(
                refreshToken = localTokenDataSource.loadRefreshToken()!!,
                accessToken = localTokenDataSource.accessToken!!
            )

        val getTokensWithNetworkResult =
            getTokensWithNetwork(localTokenDataSource.loadRefreshToken()!!)

        if (getTokensWithNetworkResult is ErrorResult) return getTokensWithNetworkResult
        if (getTokensWithNetworkResult is InterruptionResult) return getTokensWithNetworkResult

        val getTokensWithNetworkResultCast =
            getTokensWithNetworkResult as GetTokensWithNetworkResult

        val saveTokensResult = saveTokens(
            getTokensWithNetworkResultCast.refreshToken,
            getTokensWithNetworkResultCast.accessToken
        )

        if (saveTokensResult is ErrorResult) return saveTokensResult

        return GetTokensResult(
            getTokensWithNetworkResultCast.refreshToken,
            getTokensWithNetworkResultCast.accessToken
        )
    }

    fun getAccessTokenPayload(): Result {
        if (localTokenDataSource.accessToken == null) throw IllegalStateException()

        val payload = localTokenDataSource.getTokenPayload(localTokenDataSource.accessToken!!)

        if (payload == null) return ErrorResult(ErrorContext.Token.INVALID_TOKEN.id)

        return GetAccessTokenPayloadResult(payload)
    }

    suspend fun saveTokens(
        refreshToken: String,
        accessToken: String
    ): Result {
        localTokenDataSource.saveTokens(accessToken, refreshToken)

        return Result()
    }
}