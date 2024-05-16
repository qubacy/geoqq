package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.WebSocketAdapter
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class RemoteUserHttpWebSocketDataSourceImpl @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mWebSocketAdapter: WebSocketAdapter,
    private val mUserUpdatedServerEventPayloadJsonAdapter: JsonAdapter<UserUpdatedServerEventPayload>
) : RemoteUserHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {
    companion object {
        const val USER_UPDATED_EVENT_TYPE_NAME = "updated_public_user"
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*> {
        return when (type) {
            USER_UPDATED_EVENT_TYPE_NAME -> mUserUpdatedServerEventPayloadJsonAdapter
            else -> throw IllegalArgumentException()
        }
    }
}