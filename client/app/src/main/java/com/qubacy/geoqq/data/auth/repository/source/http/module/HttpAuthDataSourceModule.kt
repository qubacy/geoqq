package com.qubacy.geoqq.data.auth.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.auth.repository.source.http.HttpAuthDataSource
import com.qubacy.geoqq.data.auth.repository.source.http.api.HttpAuthDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpAuthDataSourceModule {
    @Provides
    fun provideHttpAuthDataSource(
        httpAuthDataSourceApi: HttpAuthDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpAuthDataSource {
        return HttpAuthDataSource(httpAuthDataSourceApi, httpCallExecutor)
    }
}