package com.qubacy.geoqq.data.token.repository

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.error.type.TokenErrorType
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.result.GetTokensDataResult
import javax.inject.Inject

class TokenDataRepository @Inject constructor(
    val errorDataRepository: ErrorDataRepository,
    val localTokenDataSource: LocalTokenDataSource,
    val httpTokenDataSource: HttpTokenDataSource
) : DataRepository {
    suspend fun getTokens(): GetTokensDataResult {
        val localAccessToken = localTokenDataSource.lastAccessToken
        val localRefreshToken = localTokenDataSource.getRefreshToken()

        if (localRefreshToken == null)
            throw ErrorAppException(errorDataRepository.getError(
                TokenErrorType.LOCAL_REFRESH_TOKEN_NOT_FOUND.getErrorCode()))

        val isLocalAccessTokenValid =
            if (localAccessToken != null) checkTokenForValidity(localAccessToken)
            else false

        if (isLocalAccessTokenValid)
            return GetTokensDataResult(localAccessToken!!, localRefreshToken)

        val updateTokensCall = httpTokenDataSource.updateTokens(localRefreshToken)
        val updateTokensResponse = executeNetworkRequest(errorDataRepository, updateTokensCall)

        localTokenDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )

        return GetTokensDataResult(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )
    }

    fun getAccessTokenPayload(): Map<String, Claim> {
        val accessToken = localTokenDataSource.lastAccessToken
            ?: throw IllegalStateException()
        val payload = getTokenPayload(accessToken)

        if (payload == null)
            throw ErrorAppException(errorDataRepository.getError(
                TokenErrorType.INVALID_TOKEN_PAYLOAD.getErrorCode()))

        return payload
    }

    suspend fun clearTokens() {
        localTokenDataSource.clearTokens()
    }

    fun checkTokenForValidity(token: String): Boolean {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) { return false }

        return !jwtToken.isExpired(0)
    }

    fun getTokenPayload(token: String): Map<String, Claim>? {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) { return null }

        return jwtToken.claims
    }
}