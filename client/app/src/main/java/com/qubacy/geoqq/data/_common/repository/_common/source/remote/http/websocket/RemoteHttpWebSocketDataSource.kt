package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model.closed.WebSocketClosedEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model.message.WebSocketMessageEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.callback.RemoteHttpWebSocketDataSourceCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.ServerEventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.callback.ServerEventJsonAdapterCallback

abstract class RemoteHttpWebSocketDataSource(
    private val mCallback: RemoteHttpWebSocketDataSourceCallback
) : WebSocketEventListener, ServerEventJsonAdapterCallback {
    private val mServerEventJsonAdapter: ServerEventJsonAdapter

    init {
        mServerEventJsonAdapter = ServerEventJsonAdapter(this)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        when (event::class) {
            WebSocketClosedEvent::class -> onClosedEventGotten(event as WebSocketClosedEvent)
            WebSocketErrorEvent::class -> onErrorEventGotten(event as WebSocketErrorEvent)
            WebSocketMessageEvent::class -> onMessageEventGotten(event as WebSocketMessageEvent)
        }
    }

    private fun onClosedEventGotten(closedEvent: WebSocketClosedEvent) {
        mCallback.onClosedEventOccurred()
    }

    private fun onErrorEventGotten(errorEvent: WebSocketErrorEvent) {
        mCallback.onErrorEventOccurred(errorEvent.error)
    }

    private fun onMessageEventGotten(messageEvent: WebSocketMessageEvent) {
        val serverEvent = mServerEventJsonAdapter.fromJson(messageEvent.message)!!

        mCallback.onMessageEventGotten(serverEvent.payload)
    }
}