package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common

import okhttp3.Interceptor

interface AuthorizationHttpRestInterceptor : Interceptor {
    companion object {
        const val TAG = "AuthHttpIntercptr"

        val AUTH_URL_PATH_SEGMENTS = arrayOf("sign-in", "sign-up")
    }
}