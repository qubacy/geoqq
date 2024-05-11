package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.impl.RemoteTokenHttpRestDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteTokenHttpRestDataSourceModule {
    @Provides
    fun provideRemoteTokenHttpRestDataSource(
        httpCallExecutor: HttpCallExecutor,
        httpRestApi: HttpRestApi
    ): RemoteTokenHttpRestDataSource {
        val tokenApi = httpRestApi.tokenApi

        return RemoteTokenHttpRestDataSourceImpl(httpCallExecutor).apply {
            setHttpTokenDataSourceApi(tokenApi)
        }
    }
}