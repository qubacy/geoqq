package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.module

import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl.RemoteUserHttpWebSocketDataSourceImpl
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteUserHttpWebSocketDataSourceModule {
    @Provides
    abstract fun bindRemoteUserHttpWebSocketDataSource(
        remoteUserHttpWebSocketDataSource: RemoteUserHttpWebSocketDataSourceImpl
    ): RemoteUserHttpWebSocketDataSource
}