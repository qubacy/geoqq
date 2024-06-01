package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.RemoteMateChatHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.MateChatEventPayload
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket.impl.RemoteMateChatHttpWebSocketDataSourceImpl
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteMateChatHttpWebSocketDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteMateChatHttpWebSocketDataSource(
            eventJsonAdapter: EventJsonAdapter,
            webSocketAdapter: WebSocketAdapter,
            mateChatEventPayloadJsonAdapter: JsonAdapter<MateChatEventPayload>
        ): RemoteMateChatHttpWebSocketDataSource {
            return RemoteMateChatHttpWebSocketDataSourceImpl(
                mEventJsonAdapter = eventJsonAdapter,
                webSocketAdapter = webSocketAdapter,
                mMateChatEventPayloadJsonAdapter = mateChatEventPayloadJsonAdapter
            )
        }
    }
}