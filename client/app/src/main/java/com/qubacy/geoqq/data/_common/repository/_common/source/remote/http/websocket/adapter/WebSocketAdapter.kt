package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.listener.callback.WebSocketListenerAdapterCallback
import okhttp3.WebSocket

class WebSocketAdapter(
    webSocket: WebSocket,
    listenerAdapterRef: WebSocketListenerAdapter
) : WebSocketListenerAdapterCallback {
    private val mWebSocket: WebSocket = webSocket
    private val mListenerAdapterRef: WebSocketListenerAdapter = listenerAdapterRef

    private val mEventListeners: MutableList<WebSocketEventListener> = mutableListOf()

    fun addEventListener(eventListener: WebSocketEventListener) {
        mEventListeners.add(eventListener)
    }

    fun remoteEventListener(eventListener: WebSocketEventListener) {
        mEventListeners.remove(eventListener)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        conveyEvent(event)
    }

    private fun conveyEvent(event: WebSocketEvent) {
        for (eventListener in mEventListeners)
            eventListener.onEventGotten(event)
    }
}