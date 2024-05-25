package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest.impl.RemoteMateMessageHttpRestDataSourceImpl
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api.RemoteMateMessageHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteMateMessageHttpRestDataSourceModule {
    @Provides
    fun provideRemoteMateMessageHttpRestDataSource(
        httpMateMessageDataSourceApi: RemoteMateMessageHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutorImpl
    ): RemoteMateMessageHttpRestDataSource {
        return RemoteMateMessageHttpRestDataSourceImpl(
            httpMateMessageDataSourceApi, httpCallExecutor)
    }
}