package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.RemoteGeoMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.GeoMessageAddedEventPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket.impl.RemoteGeoMessageHttpWebSocketDataSourceImpl
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteGeoMessageHttpWebSocketDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteGeoMessageWebSocketDataSource(
            eventJsonAdapter: EventJsonAdapter,
            webSocketAdapter: WebSocketAdapter,
            geoMessageAddedEventPayloadJsonAdapter: JsonAdapter<GeoMessageAddedEventPayload>
        ): RemoteGeoMessageHttpWebSocketDataSource {
            return RemoteGeoMessageHttpWebSocketDataSourceImpl(
                mEventJsonAdapter = eventJsonAdapter,
                webSocketAdapter = webSocketAdapter,
                mGeoMessageAddedEventPayloadJsonAdapter = geoMessageAddedEventPayloadJsonAdapter
            )
        }
    }
}