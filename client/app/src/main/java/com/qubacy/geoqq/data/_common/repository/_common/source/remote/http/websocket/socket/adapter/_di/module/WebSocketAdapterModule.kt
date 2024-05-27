package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter.impl.WebSocketAdapterImpl
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Qualifier
annotation class WebSocketAdapterCreateQualifier

@Module
abstract class WebSocketAdapterModule {
    @WebSocketAdapterCreateQualifier
    @Binds
    abstract fun bindWebSocketAdapter(webSocketAdapter: WebSocketAdapterImpl): WebSocketAdapter

    companion object {
        @JvmStatic
        @Provides
        fun provideWebSocketAdapter(
            authDataRepository: AuthDataRepository
        ): WebSocketAdapter {
            return authDataRepository.webSocketAdapter
        }
    }
}