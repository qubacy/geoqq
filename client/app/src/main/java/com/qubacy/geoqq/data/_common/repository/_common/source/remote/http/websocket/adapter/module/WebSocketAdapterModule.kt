package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.WebSocketAdapter
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
        @ApplicationContext context: Context
    ): WebSocketAdapter {
        val application = context as CustomApplication

        return application.webSocketAdapter
    }
}