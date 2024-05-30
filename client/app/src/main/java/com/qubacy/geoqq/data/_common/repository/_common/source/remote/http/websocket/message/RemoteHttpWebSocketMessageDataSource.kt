package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.message

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.RemoteHttpWebSocketDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.callback.EventJsonAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.WebSocketMessageEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class RemoteHttpWebSocketMessageDataSource @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : RemoteHttpWebSocketDataSource(coroutineDispatcher, coroutineScope), EventJsonAdapterCallback {
    protected abstract val mEventJsonAdapter: EventJsonAdapter

    override fun processEvent(event: WebSocketEvent): WebSocketResult? {
        if (event !is WebSocketMessageEvent) return null

        return processMessageEvent(event)
    }

    private fun processMessageEvent(event: WebSocketMessageEvent): WebSocketResult? {
        val serverEvent = mEventJsonAdapter.fromJson(event.message) ?: return null

        return WebSocketPayloadResult(serverEvent.header.type, serverEvent.payload)
    }

//    private fun onErrorEventGotten(errorEvent: WebSocketErrorEvent): WebSocketErrorResult {
//        return WebSocketErrorResult(errorEvent.error)
//    }
}