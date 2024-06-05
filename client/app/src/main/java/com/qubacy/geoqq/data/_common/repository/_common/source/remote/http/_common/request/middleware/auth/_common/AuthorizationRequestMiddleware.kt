package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common

import okhttp3.Request

interface AuthorizationRequestMiddleware {
    companion object {
        const val AUTH_TOKEN_HEADER_NAME = "Authorization"
        const val AUTH_TOKEN_HEADER_VALUE_FORMAT = "Bearer %1\$s"
    }

    fun process(request: Request): Request
}