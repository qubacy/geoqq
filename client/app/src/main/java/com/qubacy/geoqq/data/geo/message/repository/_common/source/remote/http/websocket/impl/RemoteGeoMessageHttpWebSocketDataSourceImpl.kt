package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.RemoteGeoMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.GeoMessageAddedEventPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.type.GeoMessageEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteGeoMessageHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mEventJsonAdapter: EventJsonAdapter,
    webSocketAdapter: WebSocketAdapter,
    private val mGeoMessageAddedEventPayloadJsonAdapter: JsonAdapter<GeoMessageAddedEventPayload>
) : RemoteGeoMessageHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {
    init {
        mWebSocketAdapter = webSocketAdapter

        mEventJsonAdapter.setCallback(this)
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        return when (type) {
            GeoMessageEventType.GEO_MESSAGE_ADDED_EVENT_TYPE.title ->
                mGeoMessageAddedEventPayloadJsonAdapter
            else -> null
        }
    }
}