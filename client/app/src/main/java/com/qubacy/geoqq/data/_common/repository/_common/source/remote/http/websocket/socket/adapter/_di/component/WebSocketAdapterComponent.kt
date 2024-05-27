package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._di.component

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket._di.module.WebSocketModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._di.module.WebSocketAdapterModule
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = [
    WebSocketAdapterModule::class,
    WebSocketModule::class
])
interface WebSocketAdapterComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): WebSocketAdapterComponent
    }

    fun webSocketAdapter(): WebSocketAdapter
}