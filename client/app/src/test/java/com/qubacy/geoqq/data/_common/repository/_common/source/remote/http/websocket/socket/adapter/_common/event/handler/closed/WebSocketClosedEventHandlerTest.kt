package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.closed

import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.closed.WebSocketClosedEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.closed.callback.WebSocketClosedEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.closed.WebSocketClosedEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.context.WebSocketContext
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class WebSocketClosedEventHandlerTest {
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    private lateinit var mWebSocketClosedEventHandler: WebSocketClosedEventHandler

    private var mWebSocketClosedEventHandlerCallbackOnWebSocketClosedGracefullyCallFlag = false

    @Before
    fun setup() {
        val webSocketClosedEventHandlerCallbackMock = mockWebSocketClosedEventHandlerCallback()

        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        mWebSocketClosedEventHandler = WebSocketClosedEventHandler(
            mErrorDataSourceMockContainer.errorDataSourceMock
        ).apply {
            setCallback(webSocketClosedEventHandlerCallbackMock)
        }
    }

    private fun mockWebSocketClosedEventHandlerCallback(): WebSocketClosedEventHandlerCallback {
        val webSocketClosedEventHandler = Mockito.mock(WebSocketClosedEventHandlerCallback::class.java)

        Mockito.`when`(webSocketClosedEventHandler.onWebSocketClosedGracefully()).thenAnswer {
            mWebSocketClosedEventHandlerCallbackOnWebSocketClosedGracefullyCallFlag = true

            Unit
        }

        return webSocketClosedEventHandler
    }

    @After
    fun clear() {
        mWebSocketClosedEventHandlerCallbackOnWebSocketClosedGracefullyCallFlag = false
    }

    @Test
    fun handleGracefulClosure() {
        val event = WebSocketClosedEvent(WebSocketContext.GRACEFUL_DISCONNECTION_CODE)

        mWebSocketClosedEventHandler.handle(event)

        Assert.assertTrue(mWebSocketClosedEventHandlerCallbackOnWebSocketClosedGracefullyCallFlag)
    }

    @Test
    fun handleTimeoutTest() {
        val event = WebSocketClosedEvent(WebSocketClosedEventHandler.TIMEOUT_CLOSE_CODE)
        val error = TestError.normal

        val expectedException = ErrorAppException(error)

        mErrorDataSourceMockContainer.getError = error

        try {
            mWebSocketClosedEventHandler.handle(event)

        } catch (e: ErrorAppException) {
            Assert.assertEquals(expectedException.error, e.error)

            return
        }

        throw IllegalStateException()
    }
}