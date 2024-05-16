package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl.RemoteUserHttpWebSocketDataSourceImpl
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteUserHttpWebSocketDataSourceModule {
    @Provides
    fun provideRemoteUserHttpWebSocketDataSource(
        webSocketAdapter: WebSocketAdapter,
        @ApplicationContext context: Context
    ): RemoteUserHttpWebSocketDataSource {
        val moshi = (context as CustomApplication).moshi
        val userUpdatedServerEventPayloadJsonAdapter =
            moshi.adapter(UserUpdatedServerEventPayload::class.java)

        return RemoteUserHttpWebSocketDataSourceImpl(
            mWebSocketAdapter = webSocketAdapter,
            mUserUpdatedServerEventPayloadJsonAdapter = userUpdatedServerEventPayloadJsonAdapter
        )
    }
}