package com.qubacy.geoqq.data.token.repository

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.util.base64.Base64Util
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.error.type.TokenErrorType
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.result.GetTokensDataResult
import javax.inject.Inject

class TokenDataRepository @Inject constructor(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mLocalTokenDataSource: LocalTokenDataSource,
    private val mHttpTokenDataSource: HttpTokenDataSource
) : DataRepository {
    companion object {
        const val TAG = "TokenDataRepository"
    }

    suspend fun getTokens(): GetTokensDataResult {
        val localAccessToken = mLocalTokenDataSource.lastAccessToken
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
        val updateTokensResponse = executeNetworkRequest(mErrorDataRepository, updateTokensCall)

        mLocalTokenDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )

        return GetTokensDataResult(
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
        //val passwordHash = Base64Util.bytesToString(passwordHashBytes)

        val signInRequest = mHttpTokenDataSource.signIn(login, passwordHash)
        val signInResponse = executeNetworkRequest(mErrorDataRepository, signInRequest)

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
        //val passwordHash = Base64Util.bytesToString(passwordHashBytes)

        val signUpRequest = mHttpTokenDataSource.signUp(login, passwordHash)
        val signUpResponse = executeNetworkRequest(mErrorDataRepository, signUpRequest)

        mLocalTokenDataSource.saveTokens(
            signUpResponse.accessToken,
            signUpResponse.refreshToken
        )
    }

    fun getAccessTokenPayload(): Map<String, Claim> {
        val accessToken = mLocalTokenDataSource.lastAccessToken
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