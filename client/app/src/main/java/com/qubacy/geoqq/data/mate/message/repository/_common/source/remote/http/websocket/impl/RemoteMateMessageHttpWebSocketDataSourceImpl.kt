package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.RemoteMateMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.MateMessageAddedEventPayload
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.type.MateMessageEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteMateMessageHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mEventJsonAdapter: EventJsonAdapter,
    webSocketAdapter: WebSocketAdapter,
    private val mMateMessageEventPayloadJsonAdapter: JsonAdapter<MateMessageAddedEventPayload>
) : RemoteMateMessageHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {
    init {
        mEventJsonAdapter.setCallback(this)

        mWebSocketAdapter = webSocketAdapter
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        return when (type) {
            MateMessageEventType.MATE_MESSAGE_ADDED_EVENT_TYPE.title ->
                mMateMessageEventPayloadJsonAdapter
            else -> null
        }
    }
}