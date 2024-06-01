package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.RemoteMateChatHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.MateChatEventPayload
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.type.MateChatEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteMateChatHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mEventJsonAdapter: EventJsonAdapter,
    webSocketAdapter: WebSocketAdapter,
    private val mMateChatEventPayloadJsonAdapter: JsonAdapter<MateChatEventPayload>
) : RemoteMateChatHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {
    init {
        mEventJsonAdapter.setCallback(this)

        mWebSocketAdapter = webSocketAdapter
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        return when (type) {
            MateChatEventType.MATE_CHAT_ADDED_EVENT_TYPE_NAME.title,
            MateChatEventType.MATE_CHAT_UPDATED_EVENT_TYPE_NAME.title ->
                mMateChatEventPayloadJsonAdapter
            else -> null
        }
    }
}