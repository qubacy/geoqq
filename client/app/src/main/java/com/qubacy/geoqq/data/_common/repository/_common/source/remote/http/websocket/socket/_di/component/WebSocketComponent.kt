package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket._di.component

import com.qubacy.geoqq._common._di.scope.SignedInScope
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket._di.module.WebSocketModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._di.module.WebSocketAdapterModule
import dagger.Subcomponent
import okhttp3.WebSocket

@SignedInScope
@Subcomponent(modules = [
    WebSocketModule::class,
    WebSocketAdapterModule::class
])
interface WebSocketComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): WebSocketComponent
    }

    fun webSocket(): WebSocket
}