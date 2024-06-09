package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.RemoteGeoMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.location.GeoLocationActionPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.message.GeoMessageActionPayload
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
            localErrorDatabaseDataSource: LocalErrorDatabaseDataSource,
            geoMessageAddedEventPayloadJsonAdapter: JsonAdapter<GeoMessageAddedEventPayload>,
            geoLocationActionPayloadJsonAdapter: JsonAdapter<GeoLocationActionPayload>,
            geoMessageActionPayloadJsonAdapter: JsonAdapter<GeoMessageActionPayload>
        ): RemoteGeoMessageHttpWebSocketDataSource {
            return RemoteGeoMessageHttpWebSocketDataSourceImpl(
                mEventJsonAdapter = eventJsonAdapter,
                mErrorDataSource = localErrorDatabaseDataSource,
                webSocketAdapter = webSocketAdapter,
                mGeoMessageAddedEventPayloadJsonAdapter = geoMessageAddedEventPayloadJsonAdapter,
                mGeoLocationActionPayloadJsonAdapter = geoLocationActionPayloadJsonAdapter,
                mGeoMessageActionPayloadJsonAdapter = geoMessageActionPayloadJsonAdapter
            )
        }
    }
}