package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.error

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.error.type.DataHttpWebSocketErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.error.WebSocketErrorEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.error.callback.WebSocketErrorEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class WebSocketErrorEventHandlerTest {
    private lateinit var mWebSocketErrorEventHandler: WebSocketErrorEventHandler

    private var mWebSocketErrorEventHandlerCallbackReconnectToWebSocketCallFlag = false

    @Before
    fun setup() {
        val webSocketErrorEventHandlerCallback = mockWebSocketErrorEventHandlerCallback()

        mWebSocketErrorEventHandler = WebSocketErrorEventHandler().apply {
            setCallback(webSocketErrorEventHandlerCallback)
        }
    }

    private fun mockWebSocketErrorEventHandlerCallback(): WebSocketErrorEventHandlerCallback {
        val webSocketErrorEventHandlerCallback =
            Mockito.mock(WebSocketErrorEventHandlerCallback::class.java)

        Mockito.`when`(webSocketErrorEventHandlerCallback.reconnectToWebSocket()).thenAnswer {
            mWebSocketErrorEventHandlerCallbackReconnectToWebSocketCallFlag = true

            Unit
        }

        return webSocketErrorEventHandlerCallback
    }

    @After
    fun clear() {
        mWebSocketErrorEventHandlerCallbackReconnectToWebSocketCallFlag = true
    }

    @Test
    fun handleWebSocketFailureTest() {
        val error = Error(
            DataHttpWebSocketErrorType.WEB_SOCKET_FAILURE.getErrorCode(),
            String(),
            false
        )
        val event = WebSocketErrorEvent(error)

        mWebSocketErrorEventHandler.handle(event)

        Assert.assertTrue(mWebSocketErrorEventHandlerCallbackReconnectToWebSocketCallFlag)
    }
}