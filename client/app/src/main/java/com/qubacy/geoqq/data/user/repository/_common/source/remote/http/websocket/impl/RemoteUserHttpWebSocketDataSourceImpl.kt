package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.type.UserServerEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteUserHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mEventJsonAdapter: EventJsonAdapter,
    webSocketAdapter: WebSocketAdapter,
    private val mUserUpdatedEventPayloadJsonAdapter: JsonAdapter<UserUpdatedServerEventPayload>
) : RemoteUserHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {

    init {
        mEventJsonAdapter.setCallback(this)

        mWebSocketAdapter = webSocketAdapter
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        return when (type) {
            UserServerEventType.USER_UPDATED_EVENT_TYPE_NAME.title ->
                mUserUpdatedEventPayloadJsonAdapter
            else -> null
        }
    }
}