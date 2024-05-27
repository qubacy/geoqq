package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.impl

import com.qubacy.geoqq._common.model.error.general.GeneralErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorResponseJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common.AuthorizationHttpRestInterceptor.Companion.AUTH_TOKEN_HEADER_NAME
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common.AuthorizationHttpRestInterceptor.Companion.AUTH_TOKEN_HEADER_VALUE_FORMAT
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common.AuthorizationHttpRestInterceptor.Companion.AUTH_URL_PATH_SEGMENTS
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthorizationHttpRestInterceptorImpl @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mErrorJsonAdapter: ErrorResponseJsonAdapter,
    private val mTokenDataRepository: TokenDataRepository
) : AuthorizationHttpRestInterceptor {
    private val mAuthMutex = Mutex()

    override fun intercept(chain: Chain): Response {
        val originalRequest = chain.request()

        for (authSegment in AUTH_URL_PATH_SEGMENTS)
            if (originalRequest.url().encodedPathSegments().contains(authSegment))
                return chain.proceed(originalRequest)

        lateinit var response: Response

        runBlocking {
            mAuthMutex.withLock {
                while (true) {
                    response = tryRequest(originalRequest, chain)

                    if (!response.isSuccessful && response.code() in 400 until 500) {
                        val errorBodySource = response.body()!!.source().peek()
                        val error = mErrorJsonAdapter.fromJson(errorBodySource)!!.error

                        if (error.id == GeneralErrorType.INVALID_ACCESS_TOKEN.getErrorCode())
                            continue
                    }

                    break
                }
            }
        }

        return response
    }

    private suspend fun tryRequest(
        originalRequest: Request,
        chain: Chain
    ): Response {
        val getTokensResult = mTokenDataRepository.getTokens()
        val tokenHeaderValue = AUTH_TOKEN_HEADER_VALUE_FORMAT.format(getTokensResult.accessToken)

        val request = originalRequest.newBuilder()
            .addHeader(AUTH_TOKEN_HEADER_NAME, tokenHeaderValue)
            .build()

        return chain.proceed(request)
    }
}