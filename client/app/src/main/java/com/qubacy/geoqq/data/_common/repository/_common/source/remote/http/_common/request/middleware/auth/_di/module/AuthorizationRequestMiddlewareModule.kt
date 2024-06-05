package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common.AuthorizationRequestMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth.impl.AuthorizationRequestMiddlewareImpl
import dagger.Binds
import dagger.Module

@Module
abstract class AuthorizationRequestMiddlewareModule {
    @Binds
    abstract fun bindAuthorizationRequestMiddleware(
        authorizationMiddleware: AuthorizationRequestMiddlewareImpl
    ): AuthorizationRequestMiddleware
}