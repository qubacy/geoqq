package com.qubacy.geoqq.data.token.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.signin.repository.source.network.model.response.SignInWithLoginPasswordResponse
import com.qubacy.geoqq.data.token.error.TokenErrorEnum
import com.qubacy.geoqq.data.token.repository.result.CheckAccessTokenValidityResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenExistenceResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
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
                TokenErrorEnum.LOCAL_REFRESH_TOKEN_NOT_FOUND.error)

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
            .response.body()!! as UpdateTokensResponse

        val updatedRefreshToken = responseBody.refreshToken
        val updatedAccessToken = responseBody.accessToken

        return GetTokensWithNetworkResult(updatedRefreshToken, updatedAccessToken)
    }

    suspend fun getTokens(): Result {
        val checkRefreshTokenExistenceResult = checkLocalRefreshTokenExistence()

        if (checkRefreshTokenExistenceResult is ErrorResult) return checkRefreshTokenExistenceResult
        if (!(checkRefreshTokenExistenceResult as CheckRefreshTokenExistenceResult).isExisting)
            return ErrorResult(TokenErrorEnum.LOCAL_REFRESH_TOKEN_NOT_FOUND.error)

        val checkRefreshTokenValidityResult = checkRefreshTokenValidity()

        if (checkRefreshTokenValidityResult is ErrorResult) return checkRefreshTokenValidityResult
        if (!(checkRefreshTokenValidityResult as CheckRefreshTokenValidityResult).isValid)
            return ErrorResult(TokenErrorEnum.LOCAL_REFRESH_TOKEN_INVALID.error)

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

    suspend fun saveTokens(
        refreshToken: String,
        accessToken: String
    ): Result {
        localTokenDataSource.saveTokens(accessToken, refreshToken)

        return Result()
    }
}