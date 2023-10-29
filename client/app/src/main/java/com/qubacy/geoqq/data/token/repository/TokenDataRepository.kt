package com.qubacy.geoqq.data.token.repository

import com.qubacy.geoqq.data.common.repository.DataRepository
import com.qubacy.geoqq.data.common.repository.result.common.Result
import com.qubacy.geoqq.data.common.repository.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.source.network.model.response.common.Response
import com.qubacy.geoqq.data.token.error.TokenErrorEnum
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenResult
import com.qubacy.geoqq.data.token.repository.result.UpdateAccessTokenResult
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource

class TokenDataRepository(
    val localTokenDataSource: LocalTokenDataSource,
    val networkTokenDataSource: NetworkTokenDataSource
) : DataRepository() {
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

    suspend fun updateAccessToken(): Result {
        val curRefreshToken = localTokenDataSource.loadRefreshToken()

        if (curRefreshToken == null)
            return ErrorResult(
               TokenErrorEnum.LOCAL_REFRESH_TOKEN_NOT_FOUND.error)

        val response = networkTokenDataSource.updateTokens(curRefreshToken).execute()
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