package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener.callback.WebSocketListenerAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client.auth.AuthClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.impl.ClientEventJsonAdapterImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket._di.component.WebSocketComponent
import okhttp3.WebSocket
import javax.inject.Inject

class WebSocketAdapterImpl @Inject constructor(
    listenerAdapterRef: WebSocketListenerAdapter,
    private val mClientEventJsonAdapter: ClientEventJsonAdapterImpl,
    private val mAuthClientEventMiddleware: AuthClientEventJsonMiddleware,
    webSocketComponentFactory: WebSocketComponent.Factory
) : WebSocketAdapter, WebSocketListenerAdapterCallback {
    private val mWebSocketComponent: WebSocketComponent

    private var mWebSocket: WebSocket? = null
    private val mListenerAdapterRef: WebSocketListenerAdapter = listenerAdapterRef

    private val mEventListeners: MutableList<WebSocketEventListener> = mutableListOf()

    init {
        mWebSocketComponent = webSocketComponentFactory.create()
    }

    override fun addEventListener(eventListener: WebSocketEventListener) {
        synchronized(mEventListeners) {
            mEventListeners.add(eventListener)
        }
    }

    override fun removeEventListener(eventListener: WebSocketEventListener) {
        synchronized(mEventListeners) {
            mEventListeners.remove(eventListener)
        }
    }

    override fun sendEvent(type: String, payloadString: String) {
        val middlewares = getJsonMiddlewaresForClientEvent(type)
        val eventString = mClientEventJsonAdapter.toJson(middlewares, type, payloadString)

        mWebSocket!!.send(eventString)
    }

    override fun isOpen(): Boolean {
        return mWebSocket != null
    }

    override fun open() {
        if (isOpen()) return

        mWebSocket = mWebSocketComponent.webSocket()
    }

    override fun close() {
        mListenerAdapterRef.removeCallback(this)

        // todo: init a graceful disconnection..

        mWebSocket = null
    }

    override fun getJsonMiddlewaresForClientEvent(type: String): List<ClientEventJsonMiddleware> {
        return listOf(mAuthClientEventMiddleware)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        conveyEvent(event)
    }

    private fun conveyEvent(event: WebSocketEvent) {
        synchronized(mEventListeners) {
            if (mEventListeners.isEmpty()) return

            for (eventListener in mEventListeners) eventListener.onEventGotten(event)
        }
    }
}