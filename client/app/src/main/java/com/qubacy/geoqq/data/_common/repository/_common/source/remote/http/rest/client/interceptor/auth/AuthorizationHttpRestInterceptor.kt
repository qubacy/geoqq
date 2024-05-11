package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error.general.GeneralErrorType
import com.qubacy.geoqq.data._common.repository._common.error.type.token.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository._common.util.token.TokenUtils
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthorizationHttpRestInterceptor @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mErrorJsonAdapter: ErrorJsonAdapter,
    private val mTokenDataStoreSource: LocalTokenDataStoreDataSource,
    private val mTokenHttpSource: RemoteTokenHttpRestDataSource
) : Interceptor {
    companion object {
        const val TAG = "AuthHttpIntercptr"

        const val AUTH_TOKEN_HEADER_NAME = "Authorization"
        const val AUTH_TOKEN_HEADER_VALUE_FORMAT = "Bearer %1\$s"

        val AUTH_URL_PATH_SEGMENTS = arrayOf("sign-in", "sign-up")
    }

    private val mAuthMutex = Mutex()

    override fun intercept(chain: Chain): Response {
        val originalRequest = chain.request()

        for (authSegment in AUTH_URL_PATH_SEGMENTS)
            if (originalRequest.url().encodedPathSegments().contains(authSegment))
                return chain.proceed(originalRequest)

        lateinit var response: Response
        var isRetry = false

        runBlocking {
            mAuthMutex.withLock {
                while (true) {
                    response = tryRequest(originalRequest, chain, isRetry)

                    if (!response.isSuccessful && response.code() in 400 until 500) {
                        val errorBodySource = response.body()!!.source().peek()
                        val error = mErrorJsonAdapter.fromJson(errorBodySource)!!.error

                        if (error.id == GeneralErrorType.INVALID_ACCESS_TOKEN.getErrorCode()) {
                            if (!isRetry) isRetry = true

                            continue
                        }
                    }

                    break
                }
            }
        }

        return response
    }

    private suspend fun tryRequest(
        originalRequest: Request,
        chain: Chain,
        isRetry: Boolean
    ): Response {
        val tokens = if (!isRetry) getTokens() else updateTokens()
        val tokenHeaderValue = AUTH_TOKEN_HEADER_VALUE_FORMAT.format(tokens.first)

        val request = originalRequest.newBuilder()
            .addHeader(AUTH_TOKEN_HEADER_NAME, tokenHeaderValue)
            .build()

        return chain.proceed(request)
    }

    private suspend fun getTokens(): Pair<String, String> {
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

        return updateTokens(localRefreshToken!!)
    }

    private suspend fun updateTokens(): Pair<String, String> {
        val localRefreshToken = mTokenDataStoreSource.getRefreshToken()!!

        return updateTokens(localRefreshToken)
    }

    private suspend fun updateTokens(localRefreshToken: String): Pair<String, String> {
        val updatedTokens = runUpdateTokensRequest(localRefreshToken)

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