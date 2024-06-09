package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.listener

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.closed.WebSocketClosedEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.domain.WebSocketDomainMessageEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.open.WebSocketOpenEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.listener.callback.WebSocketListenerAdapterCallback
import okhttp3.Response
import okhttp3.WebSocket
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.lang.Exception

class WebSocketListenerAdapterTest {
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    private lateinit var mWebSocketListenerAdapter: WebSocketListenerAdapter

    private var mWebSocketListenerAdapterCallbackOnEventGotten: WebSocketEvent? = null

    private var mWebSocketListenerAdapterCallbackOnEventGottenCallFlag = false

    @Before
    fun setup() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        val webSocketListenerAdapterCallbackMock = mockWebSocketListenerAdapterCallback()

        mWebSocketListenerAdapter = WebSocketListenerAdapter(
            mErrorDataSourceMockContainer.errorDataSourceMock
        ).apply {
            setCallback(webSocketListenerAdapterCallbackMock)
        }
    }

    private fun mockWebSocketListenerAdapterCallback(): WebSocketListenerAdapterCallback {
        val webSocketListenerAdapterCallbackMock =
            Mockito.mock(WebSocketListenerAdapterCallback::class.java)

        Mockito.`when`(webSocketListenerAdapterCallbackMock.onEventGotten(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketListenerAdapterCallbackOnEventGottenCallFlag = true
            mWebSocketListenerAdapterCallbackOnEventGotten = it.arguments[0] as WebSocketEvent

            Unit
        }

        return webSocketListenerAdapterCallbackMock
    }

    @After
    fun clear() {
        mWebSocketListenerAdapterCallbackOnEventGotten = null

        mWebSocketListenerAdapterCallbackOnEventGottenCallFlag = false
    }

    @Test
    fun onOpenTest() {
        val webSocketMock = Mockito.mock(WebSocket::class.java)
        val responseMock = Mockito.mock(Response::class.java)

        mWebSocketListenerAdapter.onOpen(webSocketMock, responseMock)

        Assert.assertTrue(mWebSocketListenerAdapterCallbackOnEventGottenCallFlag)
        Assert.assertEquals(WebSocketOpenEvent::class, mWebSocketListenerAdapterCallbackOnEventGotten!!::class)
    }

    @Test
    fun onMessageTest() {
        val webSocketMock = Mockito.mock(WebSocket::class.java)

        val expectedText = "test"

        mWebSocketListenerAdapter.onMessage(webSocketMock, expectedText)

        Assert.assertTrue(mWebSocketListenerAdapterCallbackOnEventGottenCallFlag)
        Assert.assertEquals(WebSocketDomainMessageEvent::class, mWebSocketListenerAdapterCallbackOnEventGotten!!::class)

        val gottenText = (mWebSocketListenerAdapterCallbackOnEventGotten!! as WebSocketDomainMessageEvent)
            .message

        Assert.assertEquals(expectedText, gottenText)
    }

    @Test
    fun onClosingTest() {
        val webSocketMock = Mockito.mock(WebSocket::class.java)
        val reason = String()

        val expectedCode = 1000

        mWebSocketListenerAdapter.onClosing(webSocketMock, expectedCode, reason)

        // todo: nothing to do for now..


    }

    @Test
    fun onClosedTest() {
        val webSocketMock = Mockito.mock(WebSocket::class.java)
        val reason = String()

        val expectedCode = 1000

        mWebSocketListenerAdapter.onClosed(webSocketMock, expectedCode, reason)

        Assert.assertTrue(mWebSocketListenerAdapterCallbackOnEventGottenCallFlag)
        Assert.assertEquals(WebSocketClosedEvent::class, mWebSocketListenerAdapterCallbackOnEventGotten!!::class)

        val gottenCode = (mWebSocketListenerAdapterCallbackOnEventGotten!! as WebSocketClosedEvent)
            .code

        Assert.assertEquals(expectedCode, gottenCode)
    }

    @Test
    fun onFailureTest() {
        val webSocketMock = Mockito.mock(WebSocket::class.java)
        val throwable = Exception()

        val expectedError = TestError.normal

        mErrorDataSourceMockContainer.getError = expectedError

        mWebSocketListenerAdapter.onFailure(webSocketMock, throwable, null)

        Assert.assertTrue(mWebSocketListenerAdapterCallbackOnEventGottenCallFlag)
        Assert.assertEquals(WebSocketErrorEvent::class, mWebSocketListenerAdapterCallbackOnEventGotten!!::class)

        val gottenError = (mWebSocketListenerAdapterCallbackOnEventGotten!! as WebSocketErrorEvent)
            .error

        Assert.assertEquals(expectedError, gottenError)
    }
}