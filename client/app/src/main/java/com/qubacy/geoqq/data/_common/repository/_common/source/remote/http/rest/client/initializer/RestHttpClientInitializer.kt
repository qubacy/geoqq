package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.initializer

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.initializer.HttpClientInitializer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import okhttp3.OkHttpClient

class RestHttpClientInitializer(
    private val mAuthorizationHttpRestInterceptor: AuthorizationHttpRestInterceptor
) : HttpClientInitializer {
    override fun initializeHttpClient(httpClientBuilder: OkHttpClient.Builder) {
        httpClientBuilder.addInterceptor(mAuthorizationHttpRestInterceptor)
    }
}