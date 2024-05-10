package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.RemoteTokenHttpRestDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthorizationHttpRestInterceptorModule {
    @Provides
    fun provideAuthorizationHttpRestInterceptor(
        errorDataSource: LocalErrorDataSource,
        errorJsonAdapter: ErrorJsonAdapter,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
        remoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSource
    ): AuthorizationHttpRestInterceptor {
        return AuthorizationHttpRestInterceptor(
            errorDataSource,
            errorJsonAdapter,
            localTokenDataStoreDataSource,
            remoteTokenHttpRestDataSource
        )
    }
}