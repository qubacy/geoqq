package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.payload.updated.UserUpdatedEventPayload
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.type.UserEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteUserHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mEventJsonAdapter: EventJsonAdapter,
    override val mErrorDataSource: LocalErrorDatabaseDataSource,
    webSocketAdapter: WebSocketAdapter,
    private val mUserUpdatedEventPayloadJsonAdapter: JsonAdapter<UserUpdatedEventPayload>
) : RemoteUserHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {

    init {
        mEventJsonAdapter.setCallback(this)

        mWebSocketAdapter = webSocketAdapter
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        return when (type) {
            UserEventType.USER_UPDATED_EVENT_TYPE_NAME.title ->
                mUserUpdatedEventPayloadJsonAdapter
            else -> null
        }
    }
}