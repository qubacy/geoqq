package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest.impl.RemoteAuthHttpRestDataSourceImpl
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.RemoteAuthHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteAuthHttpRestDataSourceModule {
    @Provides
    fun provideRemoteAuthHttpRestDataSource(
        httpAuthDataSourceApi: RemoteAuthHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutorImpl
    ): RemoteAuthHttpRestDataSource {
        return RemoteAuthHttpRestDataSourceImpl(httpAuthDataSourceApi, httpCallExecutor)
    }
}