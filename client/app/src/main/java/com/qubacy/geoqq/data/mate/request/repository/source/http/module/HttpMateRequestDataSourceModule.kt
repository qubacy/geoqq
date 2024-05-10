package com.qubacy.geoqq.data.mate.request.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.HttpMateRequestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMateRequestDataSourceModule {
    @Provides
    fun provideHttpMateRequestDataSource(
        httpMateRequestDataSourceApi: HttpMateRequestDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpMateRequestDataSource {
        return HttpMateRequestDataSource(httpMateRequestDataSourceApi, httpCallExecutor)
    }
}