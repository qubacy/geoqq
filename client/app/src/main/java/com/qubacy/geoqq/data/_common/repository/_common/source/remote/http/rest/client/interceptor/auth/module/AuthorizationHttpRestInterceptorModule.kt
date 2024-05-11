package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.RemoteTokenHttpRestDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthorizationHttpRestInterceptorModule {
    @Provides
    fun provideAuthorizationHttpRestInterceptor(
        errorDataSource: LocalErrorDatabaseDataSource,
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