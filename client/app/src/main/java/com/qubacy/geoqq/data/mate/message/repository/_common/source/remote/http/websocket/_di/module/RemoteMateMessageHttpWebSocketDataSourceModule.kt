package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.RemoteMateMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.payload.add.AddMateMessageActionPayload
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.MateMessageAddedEventPayload
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket.impl.RemoteMateMessageHttpWebSocketDataSourceImpl
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteMateMessageHttpWebSocketDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteMateMessageHttpWebSocketDataSource(
            eventJsonAdapter: EventJsonAdapter,
            webSocketAdapter: WebSocketAdapter,
            mateMessageEventPayloadJsonAdapter: JsonAdapter<MateMessageAddedEventPayload>,
            addMateMessageActionPayloadJsonAdapter: JsonAdapter<AddMateMessageActionPayload>
        ): RemoteMateMessageHttpWebSocketDataSource {
            return RemoteMateMessageHttpWebSocketDataSourceImpl(
                mEventJsonAdapter = eventJsonAdapter,
                webSocketAdapter = webSocketAdapter,
                mMateMessageEventPayloadJsonAdapter = mateMessageEventPayloadJsonAdapter,
                mAddMateMessageActionPayloadJsonAdapter = addMateMessageActionPayloadJsonAdapter
            )
        }
    }
}