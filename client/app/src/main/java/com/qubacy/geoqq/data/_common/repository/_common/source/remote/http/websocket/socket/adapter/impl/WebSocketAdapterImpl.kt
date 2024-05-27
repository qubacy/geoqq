package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorResponseJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener.callback.WebSocketListenerAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client.auth.AuthClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.impl.ClientEventJsonAdapterImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.callback.ServerEventJsonAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket._di.component.WebSocketComponent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler._common.WebSocketEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.message.error.WebSocketErrorMessageEventHandler
import com.squareup.moshi.JsonAdapter
import okhttp3.WebSocket
import javax.inject.Inject

class WebSocketAdapterImpl @Inject constructor(
    listenerAdapterRef: WebSocketListenerAdapter,
    private val mClientEventJsonAdapter: ClientEventJsonAdapterImpl,
    private val mErrorJsonAdapter: ErrorResponseJsonAdapter,
    private val mAuthClientEventMiddleware: AuthClientEventJsonMiddleware,
    private val mWebSocketErrorMessageEventHandler: WebSocketErrorMessageEventHandler,
    webSocketComponentFactory: WebSocketComponent.Factory
) : WebSocketAdapter, WebSocketListenerAdapterCallback, ServerEventJsonAdapterCallback {
    companion object {
        const val ERROR_TYPE = "error"
    }

    private val mWebSocketComponent: WebSocketComponent

    private var mWebSocket: WebSocket? = null

    private val mListenerAdapterRef: WebSocketListenerAdapter = listenerAdapterRef

    private val mEventHandlers: Array<WebSocketEventHandler<WebSocketEvent>>
    private val mEventListeners: MutableList<WebSocketEventListener> = mutableListOf()

    init {
        mWebSocketComponent = webSocketComponentFactory.create()
        mEventHandlers = generateBaseEventHandlers()
    }

    override fun generateBaseEventHandlers(): Array<WebSocketEventHandler<WebSocketEvent>> {
        return arrayOf(mWebSocketErrorMessageEventHandler as WebSocketEventHandler<WebSocketEvent>)
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
        synchronized(mWebSocketComponent) {
            val middlewares = getJsonMiddlewaresForClientEvent(type)
            val eventString = mClientEventJsonAdapter.toJson(middlewares, type, payloadString)

            mWebSocket!!.send(eventString)
        }
    }

    override fun isOpen(): Boolean {
        return mWebSocket != null
    }

    override fun open() {
        synchronized(mWebSocketComponent) {
            if (isOpen()) return

            mWebSocket = mWebSocketComponent.webSocket()
        }
    }

    override fun close() {
        synchronized(mWebSocketComponent) {
            mListenerAdapterRef.removeCallback(this)

            // todo: init a graceful disconnection..

            mWebSocket = null
        }
    }

    override fun getJsonMiddlewaresForClientEvent(type: String): List<ClientEventJsonMiddleware> {
        return listOf(mAuthClientEventMiddleware)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        processEvent(event)
    }

    override fun processEvent(event: WebSocketEvent) {
        if (processBaseEvent(event)) return

        conveyEvent(event)
    }

    private fun processBaseEvent(event: WebSocketEvent): Boolean {
        for (eventHandler in mEventHandlers)
            if (eventHandler.handle(event)) return true

        return false
    }

    private fun conveyEvent(event: WebSocketEvent) {
        synchronized(mEventListeners) {
            if (mEventListeners.isEmpty()) return

            for (eventListener in mEventListeners) eventListener.onEventGotten(event)
        }
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        if (type != ERROR_TYPE) return null

        return mErrorJsonAdapter
    }

    // todo:
    //  1. add a basic message processing method. it should be capable to work with authorization issues at least;
    //  2. provide a sending queue and synchronize the sending mechanics in order to control outgoing
    //     message flow (preventing invalid token packet spamming, etc.);
    //  3. anything else?..
}