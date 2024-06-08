package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.open

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.open.WebSocketOpenEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.open.callback.WebSocketOpenEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.open.WebSocketOpenEvent
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class WebSocketOpenEventHandlerTest {
    private lateinit var mWebSocketOpenEventHandler: WebSocketOpenEventHandler

    private var mWebSocketOpenEventHandlerCallbackOnWebSocketOpenCallFlag = false

    @Before
    fun setup() {
        val webSocketOpenEventHandlerCallbackMock = mockWebSocketOpenEventHandlerCallback()

        mWebSocketOpenEventHandler = WebSocketOpenEventHandler().apply {
            setCallback(webSocketOpenEventHandlerCallbackMock)
        }
    }

    private fun mockWebSocketOpenEventHandlerCallback(): WebSocketOpenEventHandlerCallback {
        val webSocketOpenEventHandlerCallbackMock =
            Mockito.mock(WebSocketOpenEventHandlerCallback::class.java)

        Mockito.`when`(webSocketOpenEventHandlerCallbackMock.onWebSocketOpen()).thenAnswer {
            mWebSocketOpenEventHandlerCallbackOnWebSocketOpenCallFlag = true

            Unit
        }

        return webSocketOpenEventHandlerCallbackMock
    }

    @After
    fun clear() {
        mWebSocketOpenEventHandlerCallbackOnWebSocketOpenCallFlag = false
    }

    @Test
    fun handleTest() {
        val webSocketEvent = WebSocketOpenEvent()

        mWebSocketOpenEventHandler.handle(webSocketEvent)

        Assert.assertTrue(mWebSocketOpenEventHandlerCallbackOnWebSocketOpenCallFlag)
    }
}