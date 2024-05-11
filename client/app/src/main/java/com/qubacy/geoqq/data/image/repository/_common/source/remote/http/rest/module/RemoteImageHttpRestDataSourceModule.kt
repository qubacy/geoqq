package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.RemoteImageHttpRestDataSource
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.RemoteImageHttpRestDataSourceApi
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest.impl.RemoteImageHttpRestDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteImageHttpRestDataSourceModule {
    @Provides
    fun provideRemoteImageHttpRestDataSource(
        remoteImageHttpRestDataSourceApi: RemoteImageHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): RemoteImageHttpRestDataSource {
        return RemoteImageHttpRestDataSourceImpl(
            remoteImageHttpRestDataSourceApi,
            httpCallExecutor
        )
    }
}