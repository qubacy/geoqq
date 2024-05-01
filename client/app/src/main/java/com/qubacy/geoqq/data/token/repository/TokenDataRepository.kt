package com.qubacy.geoqq.data.token.repository

import android.util.Log
import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.error.type.DataTokenErrorType
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.store.LocalStoreTokenDataSource
import com.qubacy.geoqq.data.token.repository.result.GetTokensDataResult
import com.qubacy.geoqq.data.token.repository.source.http.response.UpdateTokensResponse
import com.qubacy.geoqq.data.token.repository.source.local.database.LocalDatabaseTokenDataSource
import javax.inject.Inject

class TokenDataRepository @Inject constructor(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mLocalStoreTokenDataSource: LocalStoreTokenDataSource,
    private val mLocalDatabaseTokenDataSource: LocalDatabaseTokenDataSource,
    private val mHttpTokenDataSource: HttpTokenDataSource,
    private val mHttpCallExecutor: HttpCallExecutor
) : DataRepository {
    companion object {
        const val TAG = "TokenDataRepository"
    }

    suspend fun getTokens(): GetTokensDataResult {
        Log.d(TAG, "getTokens(): entering..")

        val localAccessToken = mLocalStoreTokenDataSource.getAccessToken()
        val localRefreshToken = mLocalStoreTokenDataSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mErrorDataRepository.getError(
                DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val isLocalAccessTokenValid =
            if (localAccessToken != null) checkTokenForValidity(localAccessToken)
            else false

        if (isLocalAccessTokenValid) {
            Log.d(TAG, "getTokens(): leaving..")

            return GetTokensDataResult(localAccessToken!!, localRefreshToken!!)
        }

        val updateTokensResponse = runUpdateTokensRequest(localRefreshToken!!)

        mLocalStoreTokenDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )

        Log.d(TAG, "getTokens(): leaving..")

        return GetTokensDataResult(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )
    }

    suspend fun signIn() {
        Log.d(TAG, "signIn(): entering..")

        val localRefreshToken = mLocalStoreTokenDataSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mErrorDataRepository.getError(
                DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val updateTokensResponse = runUpdateTokensRequest(localRefreshToken!!)

        mLocalStoreTokenDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )

        Log.d(TAG, "signIn(): leaving..")
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signIn(
        login: String,
        password: String
    ) {
        Log.d(TAG, "signIn(): entering..")

        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signInRequest = mHttpTokenDataSource.signIn(login, passwordHash)
        val signInResponse = mHttpCallExecutor.executeNetworkRequest(signInRequest)

        mLocalStoreTokenDataSource.saveTokens(
            signInResponse.accessToken,
            signInResponse.refreshToken
        )

        Log.d(TAG, "signIn(): leaving..")
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signUp(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signUpRequest = mHttpTokenDataSource.signUp(login, passwordHash)
        val signUpResponse = mHttpCallExecutor.executeNetworkRequest(signUpRequest)

        mLocalStoreTokenDataSource.saveTokens(
            signUpResponse.accessToken,
            signUpResponse.refreshToken
        )
    }

    suspend fun getAccessTokenPayload(): Map<String, Claim> {
        val accessToken = mLocalStoreTokenDataSource.getAccessToken()
            ?: throw IllegalStateException()
        val payload = getTokenPayload(accessToken)

        if (payload == null)
            throw ErrorAppException(mErrorDataRepository.getError(
                DataTokenErrorType.INVALID_TOKEN_PAYLOAD.getErrorCode()))

        return payload
    }

    suspend fun logout() {
        mLocalStoreTokenDataSource.clearTokens()
        mLocalDatabaseTokenDataSource.dropDataTables()
    }

    fun checkTokenForValidity(token: String): Boolean {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) {
            e.printStackTrace()

            return false
        }

        return !jwtToken.isExpired(10) // todo: reconsider this one;
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

    private fun runUpdateTokensRequest(localRefreshToken: String): UpdateTokensResponse {
        val updateTokensCall = mHttpTokenDataSource.updateTokens(localRefreshToken)
        val updateTokensResponse = mHttpCallExecutor.executeNetworkRequest(updateTokensCall)

        return updateTokensResponse
    }
}