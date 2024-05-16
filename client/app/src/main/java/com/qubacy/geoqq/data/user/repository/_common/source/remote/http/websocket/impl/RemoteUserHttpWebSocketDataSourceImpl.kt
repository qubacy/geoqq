package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.callback.RemoteHttpWebSocketDataSourceCallback
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.squareup.moshi.JsonAdapter

class RemoteUserHttpWebSocketDataSourceImpl(
    callback: RemoteHttpWebSocketDataSourceCallback,
    private val mUserUpdatedServerEventPayloadJsonAdapter: JsonAdapter<UserUpdatedServerEventPayload>
) : RemoteUserHttpWebSocketDataSource(callback) {
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