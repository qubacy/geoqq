package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client.auth.AuthClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.impl.WebSocketAdapterImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.ClientEventJsonAdapter
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WebSocketAdapterModule {
    @Provides
    fun provideWebSocketAdapter(
        @ApplicationContext context: Context,
        clientEventJsonAdapter: ClientEventJsonAdapter,
        authClientEventMiddleware: AuthClientEventJsonMiddleware
    ): WebSocketAdapter {
        val application = context as CustomApplication
        val webSocketContainer = application.webSocketInitContainer

        return WebSocketAdapterImpl(
            webSocketContainer.webSocket,
            webSocketContainer.webSocketListenerAdapter,
            clientEventJsonAdapter,
            authClientEventMiddleware
        ).apply {
            webSocketContainer.webSocketListenerAdapter.addCallback(this)
        }
    }
}