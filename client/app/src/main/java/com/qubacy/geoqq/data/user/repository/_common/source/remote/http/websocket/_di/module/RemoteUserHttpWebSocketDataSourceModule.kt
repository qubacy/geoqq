package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl.RemoteUserHttpWebSocketDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteUserHttpWebSocketDataSourceModule {
    @Binds
    abstract fun bindRemoteUserHttpWebSocketDataSource(
        remoteUserHttpWebSocketDataSource: RemoteUserHttpWebSocketDataSourceImpl
    ): RemoteUserHttpWebSocketDataSource
}