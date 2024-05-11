package com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.RemoteMyProfileHttpRestDataSource
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest.impl.RemoteMyProfileHttpRestDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.RemoteMyProfileHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteMyProfileHttpRestDataSourceModule {
    @Provides
    fun provideRemoteMyProfileHttpRestDataSource(
        remoteMyProfileHttpRestDataSourceApi: RemoteMyProfileHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): RemoteMyProfileHttpRestDataSource {
        return RemoteMyProfileHttpRestDataSourceImpl(
            remoteMyProfileHttpRestDataSourceApi, httpCallExecutor)
    }
}