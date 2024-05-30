package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter.impl

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.context.HttpContext
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter.impl.ActionJsonAdapterImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler._common.WebSocketEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.closed.WebSocketClosedEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.closed.callback.WebSocketClosedEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error.WebSocketErrorMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error.callback.WebSocketErrorMessageEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.success.WebSocketSuccessMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.success.callback.WebSocketSuccessMessageEventHndlrClbck
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.listener.callback.WebSocketListenerAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client.auth.AuthClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.context.WebSocketContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import javax.inject.Inject

class WebSocketAdapterImpl @Inject constructor(
    private val mWebSocketListenerAdapter: WebSocketListenerAdapter,
    private val mActionJsonAdapter: ActionJsonAdapterImpl,
    private val mAuthClientEventMiddleware: AuthClientEventJsonMiddleware,
    private val mWebSocketErrorMessageEventHandler: WebSocketErrorMessageEventHandler,
    private val mWebSocketSuccessMessageEventHandler: WebSocketSuccessMessageEventHandler,
    private val mWebSocketClosedEventHandler: WebSocketClosedEventHandler,
    private val mOkHttpClient: OkHttpClient,
) : WebSocketAdapter,
    WebSocketListenerAdapterCallback,
    WebSocketSuccessMessageEventHndlrClbck,
    WebSocketErrorMessageEventHandlerCallback,
    WebSocketClosedEventHandlerCallback
{
    private var mWebSocket: WebSocket? = null

    private val mEventHandlers: Array<WebSocketEventHandler>
    private val mEventListeners: MutableList<WebSocketEventListener> = mutableListOf()

    private var mCurrentAction: PackagedAction? = null
    private val mCurrentActionMutex: Mutex = Mutex()

    init {
        mEventHandlers = generateBaseEventHandlers()

        mWebSocketListenerAdapter.setCallback(this)

        mWebSocketSuccessMessageEventHandler.setCallback(this)
        mWebSocketErrorMessageEventHandler.setCallback(this)
        mWebSocketClosedEventHandler.setCallback(this)
    }

    override fun generateBaseEventHandlers(): Array<WebSocketEventHandler> {
        return arrayOf(
            mWebSocketErrorMessageEventHandler as WebSocketEventHandler,
            mWebSocketSuccessMessageEventHandler as WebSocketEventHandler,
            mWebSocketClosedEventHandler as WebSocketEventHandler
        )
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

//    override fun pushAction(type: String, payloadString: String) {
//        val packagedAction = PackagedAction(type, payloadString)
//
//        mActionQueue.put(packagedAction)
//
//        synchronized(mActionQueue) {
//            if (mActionQueue.isEmpty()) return sendAction(packagedAction)
//
//            mActionQueue.put(packagedAction)
//        }
//    }

    override fun sendAction(action: PackagedAction) {
        synchronized(this) {
            if (!isOpen()) return

            runBlocking {
                mCurrentActionMutex.lock() // todo: is it alright?

                mCurrentAction = action

                tryCurrentActionSending()
            }
        }
    }

    private fun tryCurrentActionSending() {
        val action = mCurrentAction!!

        val middlewares = getJsonMiddlewaresForClientEvent(action.type)
        val eventString = mActionJsonAdapter.toJson(middlewares, action)

        mWebSocket!!.send(eventString)
    }

    override fun isOpen(): Boolean {
        return mWebSocket != null
    }

    override fun open() {
        synchronized(this) {
            if (isOpen()) return

            mWebSocket = createWebSocket()
        }
    }

    private fun createWebSocket(): WebSocket {
        val request = Request.Builder().url("ws://${HttpContext.BASE_HOST_PORT}")
            .build() // todo: optimize!

        return mOkHttpClient.newWebSocket(request, mWebSocketListenerAdapter)
    }

    override fun close() {
        synchronized(this) {
            if (!isOpen()) return

            mWebSocket!!.close(WebSocketContext.GRACEFUL_DISCONNECTION_CODE, null)

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
        try {
            if (processBaseEvent(event)) return

            conveyEvent(event)

        } catch (e: ErrorAppException) {
            conveyEvent(WebSocketErrorEvent(e.error))

            if (!e.error.isCritical) mCurrentActionMutex.unlock() // todo: ?
        }
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

    override fun onWebSocketMessageSucceeded() {
        mCurrentActionMutex.unlock() // todo: ?
    }

    override fun retryActionSending() {
        tryCurrentActionSending()
    }

    // todo: alright?:
    override fun shutdownWebSocketWithError(error: Error) {
        close()
        conveyEvent(WebSocketErrorEvent(error))
    }

    override fun onWebSocketClosedGracefully() {
        // todo: do something?.. or mb not


    }
}