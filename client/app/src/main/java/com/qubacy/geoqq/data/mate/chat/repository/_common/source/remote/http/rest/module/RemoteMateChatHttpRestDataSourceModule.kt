package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.RemoteMateChatHttpRestDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest.impl.RemoteMateChatHttpRestDataSourceImpl
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.RemoteMateChatHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteMateChatHttpRestDataSourceModule {
    @Provides
    fun provideRemoteMateChatHttpRestDataSource(
        httpMateChatDataSourceApi: RemoteMateChatHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutorImpl
    ): RemoteMateChatHttpRestDataSource {
        return RemoteMateChatHttpRestDataSourceImpl(httpMateChatDataSourceApi, httpCallExecutor)
    }
}