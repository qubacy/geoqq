package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._di.module

import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.RemoteUserHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest.impl.RemoteUserHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteUserHttpRestDataSourceModule {
    @Binds
    abstract fun bindRemoteUserHttpRestDataSource(
        remoteUserHttpRestDataSource: RemoteUserHttpRestDataSourceImpl
    ): RemoteUserHttpRestDataSource
}