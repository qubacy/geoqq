package com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._di.module

import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest.impl.RemoteTokenHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteTokenHttpRestDataSourceModule {
    @Binds
    abstract fun provideRemoteTokenHttpRestDataSource(
        remoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSourceImpl
    ): RemoteTokenHttpRestDataSource
}