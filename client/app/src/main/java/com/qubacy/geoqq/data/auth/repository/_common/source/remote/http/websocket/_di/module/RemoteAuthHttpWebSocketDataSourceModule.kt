package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._common.RemoteAuthHttpWebSocketDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket.impl.RemoteAuthHttpWebSocketDataSourceImpl
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteAuthHttpWebSocketDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteAuthHttpWebSocketDataSource(): RemoteAuthHttpWebSocketDataSource {
            return RemoteAuthHttpWebSocketDataSourceImpl()
        }
    }
}