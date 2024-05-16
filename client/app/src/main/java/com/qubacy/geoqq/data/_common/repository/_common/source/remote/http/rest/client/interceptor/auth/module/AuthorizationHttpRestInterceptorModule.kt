package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthorizationHttpRestInterceptorModule {
    @Provides
    fun provideAuthorizationHttpRestInterceptor(
        localErrorDatabaseDataSource: LocalErrorDatabaseDataSource,
        errorJsonAdapter: ErrorJsonAdapter,
        tokenDataRepository: TokenDataRepository
    ): AuthorizationHttpRestInterceptor {
        return AuthorizationHttpRestInterceptor(
            localErrorDatabaseDataSource,
            errorJsonAdapter,
            tokenDataRepository
        )
    }
}