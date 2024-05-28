package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.closed

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.error.type.DataHttpWebSocketErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler._common.WebSocketEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.closed.callback.WebSocketClosedEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model.closed.WebSocketClosedEvent
import javax.inject.Inject

class WebSocketClosedEventHandler @Inject constructor(
    private val mErrorDataSource: LocalErrorDatabaseDataSource
) : WebSocketEventHandler<WebSocketEvent> {
    companion object {
        const val GRACEFUL_CLOSE_CODE = 1000
        const val TIMEOUT_CLOSE_CODE = 1001
    }

    private lateinit var mCallback: WebSocketClosedEventHandlerCallback

    fun setCallback(callback: WebSocketClosedEventHandlerCallback) {
        mCallback = callback
    }

    override fun handle(event: WebSocketEvent): Boolean {
        if (event !is WebSocketClosedEvent) return false

        when (event.code) {
            GRACEFUL_CLOSE_CODE -> mCallback.onWebSocketClosedGracefully()
            TIMEOUT_CLOSE_CODE -> throw ErrorAppException(mErrorDataSource.getError(
                DataHttpWebSocketErrorType.ACTION_TIMEOUT.getErrorCode()))
        }

        return true
    }
}