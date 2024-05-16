package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth

import com.qubacy.geoqq._common.model.error.general.GeneralErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
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
    private val mTokenDataRepository: TokenDataRepository
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