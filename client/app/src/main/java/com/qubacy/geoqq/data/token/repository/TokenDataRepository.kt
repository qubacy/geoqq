package com.qubacy.geoqq.data.token.repository

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.error.type.TokenErrorType
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.result.GetTokensDataResult
import okhttp3.OkHttpClient
import javax.inject.Inject

class TokenDataRepository @Inject constructor(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mLocalTokenDataSource: LocalTokenDataSource,
    private val mHttpTokenDataSource: HttpTokenDataSource,
    private val mHttpClient: OkHttpClient
) : DataRepository {
    companion object {
        const val TAG = "TokenDataRepository"
    }

    suspend fun getTokens(): GetTokensDataResult {
        val localAccessToken = mLocalTokenDataSource.getAccessToken()
        val localRefreshToken = mLocalTokenDataSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mErrorDataRepository.getError(
                TokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val isLocalAccessTokenValid =
            if (localAccessToken != null) checkTokenForValidity(localAccessToken)
            else false

        if (isLocalAccessTokenValid)
            return GetTokensDataResult(localAccessToken!!, localRefreshToken!!)

        val updateTokensCall = mHttpTokenDataSource.updateTokens(localRefreshToken!!)
        val updateTokensResponse = executeNetworkRequest(
            mErrorDataRepository, mHttpClient, updateTokensCall)

        mLocalTokenDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )

        return GetTokensDataResult(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )
    }

    suspend fun signIn() {
        val localRefreshToken = mLocalTokenDataSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mErrorDataRepository.getError(
                TokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val updateTokensCall = mHttpTokenDataSource.updateTokens(localRefreshToken!!)
        val updateTokensResponse = executeNetworkRequest(
            mErrorDataRepository, mHttpClient, updateTokensCall)

        mLocalTokenDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signIn(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signInRequest = mHttpTokenDataSource.signIn(login, passwordHash)
        val signInResponse = executeNetworkRequest(mErrorDataRepository, mHttpClient, signInRequest)

        mLocalTokenDataSource.saveTokens(
            signInResponse.accessToken,
            signInResponse.refreshToken
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signUp(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signUpRequest = mHttpTokenDataSource.signUp(login, passwordHash)
        val signUpResponse = executeNetworkRequest(mErrorDataRepository, mHttpClient, signUpRequest)

        mLocalTokenDataSource.saveTokens(
            signUpResponse.accessToken,
            signUpResponse.refreshToken
        )
    }

    suspend fun getAccessTokenPayload(): Map<String, Claim> {
        val accessToken = mLocalTokenDataSource.getAccessToken()
            ?: throw IllegalStateException()
        val payload = getTokenPayload(accessToken)

        if (payload == null)
            throw ErrorAppException(mErrorDataRepository.getError(
                TokenErrorType.INVALID_TOKEN_PAYLOAD.getErrorCode()))

        return payload
    }

    suspend fun clearTokens() {
        mLocalTokenDataSource.clearTokens()
    }

    fun checkTokenForValidity(token: String): Boolean {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) {
            e.printStackTrace()

            return false
        }

        return !jwtToken.isExpired(0)
    }

    fun getTokenPayload(token: String): Map<String, Claim>? {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) {
            e.printStackTrace()

            return null
        }

        return jwtToken.claims
    }
}