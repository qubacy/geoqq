package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.RemoteMateRequestHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.MateRequestAddedEventPayload
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.type.MateRequestEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteMateRequestHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mEventJsonAdapter: EventJsonAdapter,
    override val mErrorDataSource: LocalErrorDatabaseDataSource,
    webSocketAdapter: WebSocketAdapter,
    private val mMateRequestAddedEventPayloadJsonAdapter: JsonAdapter<MateRequestAddedEventPayload>
) : RemoteMateRequestHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {
    init {
        mWebSocketAdapter = webSocketAdapter

        mEventJsonAdapter.setCallback(this)
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        return when (type) {
            MateRequestEventType.MATE_REQUEST_ADDED_EVENT_TYPE.title ->
                mMateRequestAddedEventPayloadJsonAdapter
            else -> null
        }
    }
}