package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest.impl.RemoteMateRequestHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteMateRequestHttpRestDataSourceModule {
    @Binds
    abstract fun provideRemoteMateRequestHttpRestDataSource(
        remoteMateRequestHttpRestDataSource: RemoteMateRequestHttpRestDataSourceImpl
    ): RemoteMateRequestHttpRestDataSource
}