package com.qubacy.geoqq.data.user.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.http.api.HttpUserDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HttpUserDataSourceModule {
    @Provides
    fun provideHttpUserDataSource(
        httpUserDataSourceApi: HttpUserDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpUserDataSource {
        return HttpUserDataSource(httpUserDataSourceApi, httpCallExecutor)
    }
}