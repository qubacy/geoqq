package com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._di.module

import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.RemoteMyProfileHttpRestDataSource
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest.impl.RemoteMyProfileHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteMyProfileHttpRestDataSourceModule {
    @Binds
    abstract fun provideRemoteMyProfileHttpRestDataSource(
        remoteMyProfileHttpRestDataSource: RemoteMyProfileHttpRestDataSourceImpl
    ): RemoteMyProfileHttpRestDataSource
}