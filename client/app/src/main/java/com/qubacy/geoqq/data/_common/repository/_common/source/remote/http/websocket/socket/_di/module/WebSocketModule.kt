package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.context.HttpContext
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener.WebSocketListenerAdapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

@Module
abstract class WebSocketModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideWebSocket(
            localErrorDatabaseDataSource: LocalErrorDatabaseDataSource,
            okHttpClient: OkHttpClient
        ): WebSocket {
            val listener = WebSocketListenerAdapter(localErrorDatabaseDataSource)
            val request = Request.Builder().url("ws://${HttpContext.BASE_HOST_PORT}")
                .build() // todo: optimize!

            return okHttpClient.newWebSocket(request, listener)
        }
    }
}