package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.impl.LocalTokenDataStoreDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.impl.RemoteTokenHttpRestDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthorizationHttpRestInterceptorModule {
    @Provides
    fun provideAuthorizationHttpRestInterceptor(
        errorDataSource: LocalErrorDatabaseDataSourceImpl,
        errorJsonAdapter: ErrorJsonAdapter,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSourceImpl,
        remoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSourceImpl
    ): AuthorizationHttpRestInterceptor {
        return AuthorizationHttpRestInterceptor(
            errorDataSource,
            errorJsonAdapter,
            localTokenDataStoreDataSource,
            remoteTokenHttpRestDataSource
        )
    }
}