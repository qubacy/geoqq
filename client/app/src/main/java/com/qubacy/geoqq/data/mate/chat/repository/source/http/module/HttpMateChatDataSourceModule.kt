package com.qubacy.geoqq.data.mate.chat.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.HttpMateChatDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMateChatDataSourceModule {
    @Provides
    fun provideHttpMateChatDataSource(
        httpMateChatDataSourceApi: HttpMateChatDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpMateChatDataSource {
        return HttpMateChatDataSource(httpMateChatDataSourceApi, httpCallExecutor)
    }
}