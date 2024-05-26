package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.impl.AuthorizationHttpRestInterceptorImpl
import dagger.Binds
import dagger.Module

@Module
abstract class AuthorizationHttpRestInterceptorModule {
    @Binds
    abstract fun bindAuthorizationHttpRestInterceptor(
        authorizationHttpRestInterceptor: AuthorizationHttpRestInterceptorImpl
    ): AuthorizationHttpRestInterceptor
}