package com.qubacy.geoqq.data.geo.message.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
import com.qubacy.geoqq.data.geo.message.repository.source.http.api.HttpGeoChatDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpGeoChatDataSourceModule {
    @Provides
    fun provideHttpGeoChatDataSource(
        httpGeoChatDataSourceApi: HttpGeoChatDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpGeoChatDataSource {
        return HttpGeoChatDataSource(httpGeoChatDataSourceApi, httpCallExecutor)
    }
}