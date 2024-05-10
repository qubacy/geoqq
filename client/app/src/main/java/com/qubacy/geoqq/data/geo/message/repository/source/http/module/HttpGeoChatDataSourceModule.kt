package com.qubacy.geoqq.data.geo.message.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoMessageDataSource
import com.qubacy.geoqq.data.geo.message.repository.source.http.api.HttpGeoMessageDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpGeoChatDataSourceModule {
    @Provides
    fun provideHttpGeoChatDataSource(
        httpGeoMessageDataSourceApi: HttpGeoMessageDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpGeoMessageDataSource {
        return HttpGeoMessageDataSource(httpGeoMessageDataSourceApi, httpCallExecutor)
    }
}