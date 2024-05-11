package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest.impl.RemoteGeoMessageHttpRestDataSourceImpl
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.RemoteGeoMessageHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteGeoChatHttpRestDataSourceModule {
    @Provides
    fun provideRemoteGeoChatHttpRestDataSource(
        httpGeoMessageDataSourceApi: RemoteGeoMessageHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): RemoteGeoMessageHttpRestDataSource {
        return RemoteGeoMessageHttpRestDataSourceImpl(httpGeoMessageDataSourceApi, httpCallExecutor)
    }
}