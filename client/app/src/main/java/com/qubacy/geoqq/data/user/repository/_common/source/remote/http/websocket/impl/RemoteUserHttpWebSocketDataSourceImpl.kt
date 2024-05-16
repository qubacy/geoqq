package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.type.UserServerEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class RemoteUserHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class)
@Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mWebSocketAdapter: WebSocketAdapter,
    private val mUserUpdatedServerEventPayloadJsonAdapter: JsonAdapter<UserUpdatedServerEventPayload>
) : RemoteUserHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        return when (type) {
            UserServerEventType.USER_UPDATED_EVENT_TYPE_NAME.title ->
                mUserUpdatedServerEventPayloadJsonAdapter
            else -> null
        }
    }
}