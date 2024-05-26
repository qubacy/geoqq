package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.RemoteImageHttpRestDataSource
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest.impl.RemoteImageHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteImageHttpRestDataSourceModule {
    @Binds
    abstract fun bindRemoteImageHttpRestDataSource(
        remoteImageHttpRestDataSource: RemoteImageHttpRestDataSourceImpl
    ): RemoteImageHttpRestDataSource
}