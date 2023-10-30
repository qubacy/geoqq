package com.qubacy.geoqq.data.token.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.token.error.TokenErrorEnum
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenExistenceResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenResult
import com.qubacy.geoqq.data.token.repository.result.UpdateAccessTokenResult
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.model.response.UpdateTokensResponse
import retrofit2.Call
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketException

class TokenDataRepository(
    val localTokenDataSource: LocalTokenDataSource,
    val networkTokenDataSource: NetworkTokenDataSource
) : NetworkDataRepository() {
    suspend fun checkLocalRefreshTokenExistence(): Result {
        val curRefreshToken = localTokenDataSource.loadRefreshToken()

        return CheckRefreshTokenExistenceResult(curRefreshToken != null)
    }

    suspend fun checkRefreshTokenValidity(): Result {
        val curRefreshToken = localTokenDataSource.loadRefreshToken()

        if (curRefreshToken == null)
            return ErrorResult(
                TokenErrorEnum.LOCAL_REFRESH_TOKEN_NOT_FOUND.error)

        val isCurRefreshTokenValid = localTokenDataSource
            .checkRefreshTokenForValidity(curRefreshToken)

        if (!isCurRefreshTokenValid)
            return ErrorResult(
                TokenErrorEnum.REFRESH_TOKEN_EXPIRED.error)

        return CheckRefreshTokenValidityResult(curRefreshToken)
    }

    suspend fun updateTokens(): Result {
        val curRefreshToken = localTokenDataSource.loadRefreshToken()

        if (curRefreshToken == null)
            return ErrorResult(
               TokenErrorEnum.LOCAL_REFRESH_TOKEN_NOT_FOUND.error)

        var response: retrofit2.Response<UpdateTokensResponse>? = null

        try {
            mCurrentNetworkRequest = networkTokenDataSource
                .updateTokens(curRefreshToken) as Call<Response>
            response = mCurrentNetworkRequest!!
                .execute() as retrofit2.Response<UpdateTokensResponse>

        } catch (e: SocketException) { return InterruptionResult()
        } catch (e: IOException) {
            return ErrorResult(NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_FAILURE.error)
        }

        val error = retrieveNetworkError(response as retrofit2.Response<Response>)

        if (error != null) return ErrorResult(error)

        val responseBody = response.body()!!

        val refreshToken = responseBody.refreshToken
        val accessToken = responseBody.accessToken

        saveTokens(refreshToken, accessToken)

        return UpdateAccessTokenResult(refreshToken, accessToken)
    }

    fun getAccessToken(): Result {
        val accessToken = localTokenDataSource.accessToken

        if (accessToken == null)
            return ErrorResult(
                TokenErrorEnum.LOCAL_ACCESS_TOKEN_NOT_FOUND.error)

        return GetAccessTokenResult(accessToken)
    }

    suspend fun saveTokens(
        refreshToken: String,
        accessToken: String
    ): Result {
        localTokenDataSource.saveTokens(accessToken, refreshToken)

        return Result()
    }
}