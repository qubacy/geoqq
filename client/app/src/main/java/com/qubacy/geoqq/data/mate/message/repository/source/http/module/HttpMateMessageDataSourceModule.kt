package com.qubacy.geoqq.data.mate.message.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.http.api.HttpMateMessageDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMateMessageDataSourceModule {
    @Provides
    fun provideHttpMateMessageDataSource(
        httpMateMessageDataSourceApi: HttpMateMessageDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpMateMessageDataSource {
        return HttpMateMessageDataSource(httpMateMessageDataSourceApi, httpCallExecutor)
    }
}