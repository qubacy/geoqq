package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._di.module

import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest.impl.RemoteGeoMessageHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteGeoMessageHttpRestDataSourceModule {
    @Binds
    abstract fun bindRemoteGeoChatHttpRestDataSource(
        remoteGeoMessageHttpRestDataSource: RemoteGeoMessageHttpRestDataSourceImpl
    ): RemoteGeoMessageHttpRestDataSource
}