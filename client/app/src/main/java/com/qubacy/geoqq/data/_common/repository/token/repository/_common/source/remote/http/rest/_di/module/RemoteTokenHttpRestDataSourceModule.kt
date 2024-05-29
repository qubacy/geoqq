package com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._di.module

import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest.impl.RemoteTokenHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RemoteTokenHttpRestDataSourceModule {
    @Singleton
    @Binds
    abstract fun provideRemoteTokenHttpRestDataSource(
        remoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSourceImpl
    ): RemoteTokenHttpRestDataSource
}