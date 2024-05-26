package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest.module

import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest.impl.RemoteAuthHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteAuthHttpRestDataSourceModule {
    @Binds
    abstract fun bindRemoteAuthHttpRestDataSource(
        remoteAuthHttpRestDataSource: RemoteAuthHttpRestDataSourceImpl
    ): RemoteAuthHttpRestDataSource
}