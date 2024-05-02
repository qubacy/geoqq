package com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.auth

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.error.type.token.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import com.qubacy.geoqq.data._common.repository._common.util.token.TokenUtils
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationHttpInterceptor @Inject constructor(
    private val mErrorSource: LocalErrorDataSource,
    private val mTokenDataStoreSource: LocalTokenDataStoreDataSource,
    private val mTokenHttpSource: HttpTokenDataSource
) : Interceptor {
    companion object {
        const val AUTH_TOKEN_HEADER_NAME = "Authorization"
        const val AUTH_TOKEN_HEADER_VALUE_FORMAT = "Bearer %1"

        val AUTH_URL_PATH_SEGMENTS = arrayOf("sign-in", "sign-up")
    }

    private val mAuthMutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        for (authSegment in AUTH_URL_PATH_SEGMENTS)
            if (originalRequest.url().encodedPathSegments().contains(authSegment))
                return chain.proceed(originalRequest)

        lateinit var tokens: Pair<String, String>

        runBlocking {
            mAuthMutex.withLock {
                tokens = getTokens()
            }
        }

        val tokenHeaderValue = AUTH_TOKEN_HEADER_VALUE_FORMAT.format(tokens.first)

        val request = originalRequest.newBuilder()
            .addHeader(AUTH_TOKEN_HEADER_NAME, tokenHeaderValue)
            .build()

        return chain.proceed(request)
    }

    suspend fun getTokens(): Pair<String, String> {
        val localAccessToken = mTokenDataStoreSource.getAccessToken()
        val localRefreshToken = mTokenDataStoreSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) TokenUtils.checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mErrorSource.getError(
                DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val isLocalAccessTokenValid =
            if (localAccessToken != null) TokenUtils.checkTokenForValidity(localAccessToken)
            else false

        if (isLocalAccessTokenValid)
            return Pair(localAccessToken!!, localRefreshToken!!)

        val updatedTokens = runUpdateTokensRequest(localRefreshToken!!)

        mTokenDataStoreSource.saveTokens(
            updatedTokens.first,
            updatedTokens.second
        )

        return updatedTokens
    }

    private fun runUpdateTokensRequest(localRefreshToken: String): Pair<String, String> {
        val updateTokensResponse = mTokenHttpSource.updateTokens(localRefreshToken)

        return Pair(updateTokensResponse.accessToken, updateTokensResponse.refreshToken)
    }
}