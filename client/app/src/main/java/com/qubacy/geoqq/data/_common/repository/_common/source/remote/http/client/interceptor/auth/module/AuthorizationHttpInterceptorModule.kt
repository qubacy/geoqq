package com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.auth.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.auth.AuthorizationHttpInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthorizationHttpInterceptorModule {
    @Provides
    fun provideAuthorizationHttpInterceptor(
        errorDataSource: LocalErrorDataSource,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
        httpTokenDataSource: HttpTokenDataSource
    ): AuthorizationHttpInterceptor {
        return AuthorizationHttpInterceptor(
            errorDataSource,
            localTokenDataStoreDataSource,
            httpTokenDataSource
        )
    }
}