package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest.impl.RemoteMateRequestHttpRestDataSourceImpl
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.RemoteMateRequestHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteMateRequestHttpRestDataSourceModule {
    @Provides
    fun provideRemoteMateRequestHttpRestDataSource(
        remoteMateRequestHttpRestDataSourceApi: RemoteMateRequestHttpRestDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): RemoteMateRequestHttpRestDataSource {
        return RemoteMateRequestHttpRestDataSourceImpl(
            remoteMateRequestHttpRestDataSourceApi, httpCallExecutor)
    }
}