package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl.RemoteUserHttpWebSocketDataSourceImpl
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteUserHttpWebSocketDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteUserHttpWebSocketDataSource(
            eventJsonAdapter: EventJsonAdapter,
            webSocketAdapter: WebSocketAdapter,
            userUpdatedEventPayloadJsonAdapter: JsonAdapter<UserUpdatedServerEventPayload>
        ): RemoteUserHttpWebSocketDataSource {
            return RemoteUserHttpWebSocketDataSourceImpl(
                mEventJsonAdapter = eventJsonAdapter,
                mWebSocketAdapter = webSocketAdapter,
                mUserUpdatedEventPayloadJsonAdapter = userUpdatedEventPayloadJsonAdapter
            )
        }
    }
}