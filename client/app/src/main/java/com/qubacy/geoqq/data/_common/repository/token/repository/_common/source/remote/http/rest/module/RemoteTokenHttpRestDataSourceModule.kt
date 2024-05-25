package com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest.impl.RemoteTokenHttpRestDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteTokenHttpRestDataSourceModule {
    @Provides
    fun provideRemoteTokenHttpRestDataSource(
        httpCallExecutor: HttpCallExecutorImpl,
        httpRestApi: HttpRestApi
    ): RemoteTokenHttpRestDataSource {
        val tokenApi = httpRestApi.tokenApi

        return RemoteTokenHttpRestDataSourceImpl(httpCallExecutor).apply {
            setHttpTokenDataSourceApi(tokenApi)
        }
    }
}