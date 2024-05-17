package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.listener.callback.WebSocketListenerAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client.auth.AuthClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.ClientEventJsonAdapter
import okhttp3.WebSocket

class WebSocketAdapterImpl(
    webSocket: WebSocket,
    listenerAdapterRef: WebSocketListenerAdapter,
    private val mClientEventJsonAdapter: ClientEventJsonAdapter,
    private val mAuthClientEventMiddleware: AuthClientEventJsonMiddleware
) : WebSocketAdapter, WebSocketListenerAdapterCallback {
    private val mWebSocket: WebSocket = webSocket
    private val mListenerAdapterRef: WebSocketListenerAdapter = listenerAdapterRef

    private val mEventListeners: MutableList<WebSocketEventListener> = mutableListOf()

    override fun addEventListener(eventListener: WebSocketEventListener) {
        mEventListeners.add(eventListener)
    }

    override fun removeEventListener(eventListener: WebSocketEventListener) {
        mEventListeners.remove(eventListener)
    }

    override fun sendEvent(type: String, payloadString: String) {
        val middlewares = getJsonMiddlewaresForClientEvent(type)
        val eventString = mClientEventJsonAdapter.toJson(middlewares, type, payloadString)

        mWebSocket.send(eventString)
    }

    override fun close() {
        mListenerAdapterRef.removeCallback(this)

        // todo: init a graceful disconnection..
    }

    override fun getJsonMiddlewaresForClientEvent(type: String): List<ClientEventJsonMiddleware> {
        return listOf(mAuthClientEventMiddleware)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        conveyEvent(event)
    }

    private fun conveyEvent(event: WebSocketEvent) {
        if (mEventListeners.isEmpty()) return

        for (eventListener in mEventListeners) eventListener.onEventGotten(event)
    }
}