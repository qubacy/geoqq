package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.initializer.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.initializer.RestHttpClientInitializer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RestHttpClientInitializerModule {
    @Provides
    fun provideRestHttpClientInitializer(
        authorizationHttpRestInterceptor: AuthorizationHttpRestInterceptor
    ): RestHttpClientInitializer {
        return RestHttpClientInitializer(authorizationHttpRestInterceptor)
    }
}