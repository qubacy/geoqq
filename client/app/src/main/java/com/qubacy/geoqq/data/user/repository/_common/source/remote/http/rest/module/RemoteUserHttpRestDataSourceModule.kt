package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.RemoteUserHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest.impl.RemoteUserHttpRestDataSourceImpl
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.RemoteUserHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteUserHttpRestDataSourceModule {
    @Provides
    fun provideRemoteUserHttpRestDataSource(
        remoteUserDataHttpRestDataSourceApi: RemoteUserHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutorImpl
    ): RemoteUserHttpRestDataSource {
        return RemoteUserHttpRestDataSourceImpl(
            remoteUserDataHttpRestDataSourceApi, httpCallExecutor)
    }
}