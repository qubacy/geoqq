package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.impl.WebSocketAdapterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class WebSocketAdapterModule {
    @Binds
    abstract fun bindWebSocketAdapter(webSocketAdapter: WebSocketAdapterImpl): WebSocketAdapter
}