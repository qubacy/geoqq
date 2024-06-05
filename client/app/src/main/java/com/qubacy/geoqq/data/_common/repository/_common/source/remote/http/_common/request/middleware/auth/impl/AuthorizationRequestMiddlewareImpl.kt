package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common.AuthorizationRequestMiddleware
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import javax.inject.Inject

class AuthorizationRequestMiddlewareImpl @Inject constructor(
    private val mTokenDataRepository: TokenDataRepository
) : AuthorizationRequestMiddleware {
    override fun process(request: Request): Request = runBlocking {
        val token = mTokenDataRepository.getTokens().accessToken
        val authHeaderValue = AuthorizationRequestMiddleware
            .AUTH_TOKEN_HEADER_VALUE_FORMAT.format(token)

        request
            .newBuilder()
            .addHeader(AuthorizationRequestMiddleware.AUTH_TOKEN_HEADER_NAME, authHeaderValue)
            .build()
    }
}