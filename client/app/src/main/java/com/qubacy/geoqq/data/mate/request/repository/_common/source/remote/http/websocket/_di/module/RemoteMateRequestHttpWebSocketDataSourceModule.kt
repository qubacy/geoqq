package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.RemoteMateRequestHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.MateRequestAddedEventPayload
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket.impl.RemoteMateRequestHttpWebSocketDataSourceImpl
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides

@Module
class RemoteMateRequestHttpWebSocketDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteMateRequestHttpWebSocketDataSource(
            eventJsonAdapter: EventJsonAdapter,
            webSocketAdapter: WebSocketAdapter,
            mateRequestAddedEventPayloadJsonAdapter: JsonAdapter<MateRequestAddedEventPayload>
        ): RemoteMateRequestHttpWebSocketDataSource {
            return RemoteMateRequestHttpWebSocketDataSourceImpl(
                mEventJsonAdapter = eventJsonAdapter,
                webSocketAdapter = webSocketAdapter,
                mMateRequestAddedEventPayloadJsonAdapter = mateRequestAddedEventPayloadJsonAdapter
            )
        }
    }
}