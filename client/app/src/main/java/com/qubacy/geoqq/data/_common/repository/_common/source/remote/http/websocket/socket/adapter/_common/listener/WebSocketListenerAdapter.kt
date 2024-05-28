package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener

import android.util.Log
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.error.type.DataHttpWebSocketErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model.closed.WebSocketClosedEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model.message.WebSocketMessageEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener.callback.WebSocketListenerAdapterCallback
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class WebSocketListenerAdapter @Inject constructor(
    private val mLocalErrorDatabaseDataSource: LocalErrorDatabaseDataSource
) : WebSocketListener() {
    companion object {
        const val TAG = "WebSocketListnrAdptr"
    }

    private lateinit var mCallback: WebSocketListenerAdapterCallback

    fun setCallback(callback: WebSocketListenerAdapterCallback) {
        mCallback = callback
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

        // todo: nothing to do?
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)

        emitEvent(WebSocketMessageEvent(text))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)

        // todo: implement the graceful connection closure..


    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)

        Log.d(TAG, "onClosed(): code = $code; reason = $reason;")

        emitEvent(WebSocketClosedEvent(code))
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)

        Log.d(TAG, "onFailure(): reason = ${t.message};")

        val error = mLocalErrorDatabaseDataSource
            .getError(DataHttpWebSocketErrorType.WEB_SOCKET_FAILURE.getErrorCode())

        emitEvent(WebSocketErrorEvent(error))
    }

    private fun emitEvent(event: WebSocketEvent) {
        mCallback.onEventGotten(event)
    }
}