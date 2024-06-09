package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter.impl

import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client._test.mock.OkHttpClientMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common.AuthorizationRequestMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._common.ActionJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.ErrorEventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.closed.WebSocketClosedEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.error.WebSocketErrorEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error.WebSocketErrorMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.success.WebSocketSuccessMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.open.WebSocketOpenEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.general.error.WebSocketErrorMessageEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client.auth.AuthActionJsonMiddleware
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter.impl.WebSocketAdapterImpl
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.runTest
import okhttp3.Request
import okhttp3.WebSocket
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

class WebSocketAdapterImplTest {
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mOkHttpClientMockContainer: OkHttpClientMockContainer

    private lateinit var mWebSocketAdapterImpl: WebSocketAdapterImpl

    private lateinit var mWebSocketAdapterEventListenersFieldReflection: Field
    private lateinit var mWebSocketAdapterWebSocketFieldReflection: Field
    private lateinit var mCurrentActionMutexFieldReflection: Field
    private lateinit var mReconnectionCounterFieldReflection: Field

    private var mWebSocketClosedEventHandlerHandleException: ErrorAppException? = null

    private var mAuthorizationRequestMiddlewareProcessCallFlag = false

    private var mActionJsonAdapterToJsonCallFlag = false

    private var mAuthActionJsonMiddlewareProcessCallFlag = false

    private var mWebSocketErrorMessageEventHandlerHandle: Boolean? = null

    private var mWebSocketErrorMessageEventHandlerHandleCallFlag = false

    private var mWebSocketSuccessMessageEventHandlerHandle: Boolean? = null

    private var mWebSocketSuccessMessageEventHandlerHandleCallFlag = false

    private var mWebSocketClosedEventHandlerHandle: Boolean? = null

    private var mWebSocketClosedEventHandlerHandleCallFlag = true

    private var mWebSocketOpenEventHandlerHandle: Boolean? = null

    private var mWebSocketOpenEventHandlerHandleCallFlag = false

    private var mWebSocketErrorEventHandlerHandle: Boolean? = null

    private var mWebSocketErrorEventHandlerHandleCallFlag = false

    private var mWebSocketEventListenerOnEventGotten: WebSocketEvent? = null

    @Before
    fun setup() {
        mWebSocketAdapterEventListenersFieldReflection = WebSocketAdapterImpl::class.java
            .getDeclaredField("mEventListeners").apply { isAccessible = true }
        mWebSocketAdapterWebSocketFieldReflection = WebSocketAdapterImpl::class.java
            .getDeclaredField("mWebSocket").apply { isAccessible = true }
        mCurrentActionMutexFieldReflection = WebSocketAdapterImpl::class.java
            .getDeclaredField("mCurrentActionMutex").apply { isAccessible = true }
        mReconnectionCounterFieldReflection = WebSocketAdapterImpl::class.java
            .getDeclaredField("mReconnectionCounter").apply { isAccessible = true }

        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mOkHttpClientMockContainer = OkHttpClientMockContainer()

        val webSocketListenerAdapterMock = mockWebSocketListenerAdapter()
        val authorizationRequestMiddlewareMock = mockAuthorizationRequestMiddleware()
        val actionJsonAdapterMock = mockActionJsonAdapter()
        val authActionJsonMiddlewareMock = mockAuthActionJsonMiddleware()
        val webSocketErrorMessageEventHandlerMock = mockWebSocketErrorMessageEventHandler()
        val webSocketSuccessMessageEventHandlerMock = mockWebSocketSuccessMessageEventHandler()
        val webSocketClosedEventHandlerMock = mockWebSocketClosedEventHandler()
        val webSocketOpenEventHandlerMock = mockWebSocketOpenEventHandler()
        val webSocketErrorEventHandlerMock = mockWebSocketErrorEventHandler()

        mWebSocketAdapterImpl = WebSocketAdapterImpl(
            mErrorDataSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mWebSocketListenerAdapter = webSocketListenerAdapterMock,
            mAuthorizationRequestMiddleware = authorizationRequestMiddlewareMock,
            mActionJsonAdapter = actionJsonAdapterMock,
            mAuthActionJsonMiddleware = authActionJsonMiddlewareMock,
            mWebSocketErrorMessageEventHandler = webSocketErrorMessageEventHandlerMock,
            mWebSocketSuccessMessageEventHandler = webSocketSuccessMessageEventHandlerMock,
            mWebSocketClosedEventHandler = webSocketClosedEventHandlerMock,
            mWebSocketOpenEventHandler = webSocketOpenEventHandlerMock,
            mWebSocketErrorEventHandler = webSocketErrorEventHandlerMock,
            mOkHttpClient = mOkHttpClientMockContainer.okHttpClientMock
        )
    }

    private fun mockWebSocketListenerAdapter(): WebSocketListenerAdapter {
        val webSocketEventListenerAdapterMock = Mockito.mock(WebSocketListenerAdapter::class.java)

        return webSocketEventListenerAdapterMock
    }

    private fun mockAuthorizationRequestMiddleware(): AuthorizationRequestMiddleware {
        val authorizationRequestMiddlewareMock =
            Mockito.mock(AuthorizationRequestMiddleware::class.java)

        Mockito.`when`(authorizationRequestMiddlewareMock.process(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            val request = it.arguments[0] as Request

            mAuthorizationRequestMiddlewareProcessCallFlag = true

            request
        }

        return authorizationRequestMiddlewareMock
    }

    private fun mockActionJsonAdapter(): ActionJsonAdapter {
        val actionJsonAdapterMock = Mockito.mock(ActionJsonAdapter::class.java)

        Mockito.`when`(actionJsonAdapterMock.toJson(
            AnyMockUtil.anyObject(), AnyMockUtil.anyObject()
        )).thenAnswer {
            mActionJsonAdapterToJsonCallFlag = true

            String()
        }

        return actionJsonAdapterMock
    }

    private fun mockAuthActionJsonMiddleware(): AuthActionJsonMiddleware {
        val authActionJsonMiddlewareMock = Mockito.mock(AuthActionJsonMiddleware::class.java)

        Mockito.`when`(authActionJsonMiddlewareMock.process(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mAuthActionJsonMiddlewareProcessCallFlag = true

            Unit
        }

        return authActionJsonMiddlewareMock
    }

    private fun mockWebSocketErrorMessageEventHandler(): WebSocketErrorMessageEventHandler {
        val webSocketErrorMessageEventHandlerMock =
            Mockito.mock(WebSocketErrorMessageEventHandler::class.java)

        Mockito.`when`(webSocketErrorMessageEventHandlerMock.handle(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketErrorMessageEventHandlerHandleCallFlag = true
            mWebSocketErrorMessageEventHandlerHandle
        }

        return webSocketErrorMessageEventHandlerMock
    }

    private fun mockWebSocketSuccessMessageEventHandler(): WebSocketSuccessMessageEventHandler {
        val webSocketSuccessMessageEventHandlerMock =
            Mockito.mock(WebSocketSuccessMessageEventHandler::class.java)

        Mockito.`when`(webSocketSuccessMessageEventHandlerMock.handle(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketSuccessMessageEventHandlerHandleCallFlag = true
            mWebSocketSuccessMessageEventHandlerHandle
        }

        return webSocketSuccessMessageEventHandlerMock
    }

    private fun mockWebSocketClosedEventHandler(): WebSocketClosedEventHandler {
        val webSocketClosedEventHandlerMock = Mockito.mock(WebSocketClosedEventHandler::class.java)

        Mockito.`when`(webSocketClosedEventHandlerMock.handle(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketClosedEventHandlerHandleCallFlag = true

            if (mWebSocketClosedEventHandlerHandleException != null)
                throw mWebSocketClosedEventHandlerHandleException!!

            mWebSocketClosedEventHandlerHandle
        }

        return webSocketClosedEventHandlerMock
    }

    private fun mockWebSocketOpenEventHandler(): WebSocketOpenEventHandler {
        val webSocketOpenEventHandlerMock = Mockito.mock(WebSocketOpenEventHandler::class.java)

        Mockito.`when`(webSocketOpenEventHandlerMock.handle(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketOpenEventHandlerHandleCallFlag = true
            mWebSocketOpenEventHandlerHandle
        }

        return webSocketOpenEventHandlerMock
    }

    private fun mockWebSocketErrorEventHandler(): WebSocketErrorEventHandler {
        val webSocketErrorEventHandlerMock = Mockito.mock(WebSocketErrorEventHandler::class.java)

        Mockito.`when`(webSocketErrorEventHandlerMock.handle(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketErrorEventHandlerHandleCallFlag = true
            mWebSocketErrorEventHandlerHandle
        }

        return webSocketErrorEventHandlerMock
    }

    @After
    fun clear() {
        mAuthorizationRequestMiddlewareProcessCallFlag = false

        mActionJsonAdapterToJsonCallFlag = false

        mAuthActionJsonMiddlewareProcessCallFlag = false

        mWebSocketErrorMessageEventHandlerHandle = null

        mWebSocketErrorMessageEventHandlerHandleCallFlag = false

        mWebSocketSuccessMessageEventHandlerHandle = null

        mWebSocketSuccessMessageEventHandlerHandleCallFlag = false

        mWebSocketClosedEventHandlerHandle = null

        mWebSocketClosedEventHandlerHandleCallFlag = false

        mWebSocketOpenEventHandlerHandle = null

        mWebSocketOpenEventHandlerHandleCallFlag = false

        mWebSocketErrorEventHandlerHandle = null

        mWebSocketErrorEventHandlerHandleCallFlag = false

        mWebSocketEventListenerOnEventGotten = null

        mWebSocketClosedEventHandlerHandleException = null
    }

    @Test
    fun addEventListenerTest() {
        val eventListenerMock = Mockito.mock(WebSocketEventListener::class.java)

        val expectedEventListeners = listOf(eventListenerMock)

        mWebSocketAdapterImpl.addEventListener(eventListenerMock)

        val gottenEventListeners = getEventListeners()

        AssertUtils.assertEqualContent(expectedEventListeners, gottenEventListeners)
    }

    @Test
    fun removeEventListenerTest() {
        val eventListenerMock = Mockito.mock(WebSocketEventListener::class.java)

        val expectedEventListeners = listOf<WebSocketEventListener>()

        mWebSocketAdapterImpl.addEventListener(eventListenerMock)
        mWebSocketAdapterImpl.removeEventListener(eventListenerMock)

        val gottenEventListeners = getEventListeners()

        AssertUtils.assertEqualContent(expectedEventListeners, gottenEventListeners)
    }

    @Test
    fun sendActionTest() {
        val action = PackagedAction(String(), String())

        mWebSocketAdapterImpl.open()
        mWebSocketAdapterImpl.sendAction(action)

        Assert.assertTrue(mActionJsonAdapterToJsonCallFlag)
        Assert.assertTrue(mOkHttpClientMockContainer.webSocketSendCallFlag)
    }

    @Test
    fun isOpenTest() {
        val expectedInitIsOpen = false
        val expectedFinalIsOpen = true

        mWebSocketAdapterImpl.close()

        val gottenInitIsOpen = mWebSocketAdapterImpl.isOpen()

        Assert.assertEquals(expectedInitIsOpen, gottenInitIsOpen)

        mWebSocketAdapterImpl.open()

        val gottenFinalIsOpen = mWebSocketAdapterImpl.isOpen()

        Assert.assertEquals(expectedFinalIsOpen, gottenFinalIsOpen)
    }

    @Test
    fun openTest() {
        val expectedWebSocket = mOkHttpClientMockContainer.webSocketMock

        mWebSocketAdapterImpl.open()

        val gottenWebSocket = getWebSocket()

        Assert.assertTrue(mAuthorizationRequestMiddlewareProcessCallFlag)
        Assert.assertTrue(mOkHttpClientMockContainer.okHttpClientNewWebSocketCallFlag)
        Assert.assertEquals(expectedWebSocket, gottenWebSocket)
    }

    @Test
    fun closeTest() {
        val expectedWebSocket: WebSocket? = null

        mWebSocketAdapterImpl.open()
        mWebSocketAdapterImpl.close()

        val gottenWebSocket = getWebSocket()

        Assert.assertEquals(expectedWebSocket, gottenWebSocket)
    }

    @Test
    fun processBaseEventTest() {
        initWebSocketEventListener()

        val webSocketEvent = Mockito.mock(WebSocketEvent::class.java)

        mWebSocketOpenEventHandlerHandle = true

        mWebSocketAdapterImpl.processEvent(webSocketEvent)

        Assert.assertTrue(mWebSocketOpenEventHandlerHandleCallFlag)
        Assert.assertNull(mWebSocketEventListenerOnEventGotten)
    }

    @Test
    fun processEventWithConveyingTest() {
        initWebSocketEventListener()

        val webSocketEvent = Mockito.mock(WebSocketEvent::class.java)

        mWebSocketAdapterImpl.processEvent(webSocketEvent)

        Assert.assertTrue(mWebSocketOpenEventHandlerHandleCallFlag)
        Assert.assertNotNull(mWebSocketEventListenerOnEventGotten)
    }

    @Test
    fun processEventWithThrowingTest() {
        initWebSocketEventListener()

        val webSocketEvent = Mockito.mock(WebSocketEvent::class.java)

        val expectedError = TestError.normal

        mWebSocketClosedEventHandlerHandleException = ErrorAppException(expectedError)

        mWebSocketAdapterImpl.processEvent(webSocketEvent)

        val gottenError = (mWebSocketEventListenerOnEventGotten!! as WebSocketErrorEvent).error

        Assert.assertEquals(expectedError, gottenError)
    }

    @Test
    fun onWebSocketMessageSucceededTest() = runTest {
        val currentActionMutex = getCurrentActionMutex()

        currentActionMutex.lock()

        mWebSocketAdapterImpl.onWebSocketMessageSucceeded()

        Assert.assertFalse(currentActionMutex.isLocked)
    }

    @Test
    fun retryActionSendingTest() {
        // nothing to test for now. just calls .tryCurrentActionSending();


    }

    @Test
    fun shutdownWebSocketWithErrorTest() {
        initWebSocketEventListener()

        val expectedWebSocket: WebSocket? = null
        val expectedError = TestError.normal

        mWebSocketAdapterImpl.open()
        mWebSocketAdapterImpl.shutdownWebSocketWithError(expectedError)

        val gottenWebSocket = getWebSocket()
        val gottenWebSocketEvent = mWebSocketEventListenerOnEventGotten!!

        Assert.assertEquals(expectedWebSocket, gottenWebSocket)
        Assert.assertEquals(WebSocketErrorEvent::class, gottenWebSocketEvent::class)

        gottenWebSocketEvent as WebSocketErrorEvent

        Assert.assertEquals(expectedError, gottenWebSocketEvent.error)
    }

    @Test
    fun onWebSocketClosedGracefullyTest() {
        // todo: nothing to do for now..


    }

    @Test
    fun reconnectToWebSocketTest() {
        val expectedReconnectionCounter = 1

        mWebSocketAdapterImpl.reconnectToWebSocket()

        val gottenReconnectionCounter = getReconnectionCounter()

        Assert.assertTrue(mOkHttpClientMockContainer.okHttpClientNewWebSocketCallFlag)
        Assert.assertEquals(expectedReconnectionCounter, gottenReconnectionCounter)
    }

    @Test
    fun onWebSocketOpenTest() {
        val expectedReconnectionCounter = 0

        mWebSocketAdapterImpl.onWebSocketOpen()

        val gottenReconnectionCounter = getReconnectionCounter()

        Assert.assertEquals(expectedReconnectionCounter, gottenReconnectionCounter)
    }

    @Test
    fun conveyMessageErrorTest() = runTest {
        initWebSocketEventListener()

        val type = "test"
        val errorEventPayload = ErrorEventPayload(0, ErrorResponseContent(0))

        val currentActionMutex = getCurrentActionMutex()

        val expectedMessageEvent = WebSocketErrorMessageEvent(type, errorEventPayload)

        currentActionMutex.lock()

        mWebSocketAdapterImpl.conveyMessageError(type, errorEventPayload)

        val gottenMessageEvent = mWebSocketEventListenerOnEventGotten!!

        Assert.assertFalse(currentActionMutex.isLocked)
        Assert.assertEquals(WebSocketErrorMessageEvent::class, gottenMessageEvent::class)
        Assert.assertEquals(expectedMessageEvent, gottenMessageEvent)
    }

    private fun initWebSocketEventListener() {
        val webSocketEventListenerMock = Mockito.mock(WebSocketEventListener::class.java)

        Mockito.`when`(webSocketEventListenerMock.onEventGotten(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketEventListenerOnEventGotten = it.arguments[0] as WebSocketEvent

            Unit
        }

        mWebSocketAdapterImpl.addEventListener(webSocketEventListenerMock)
    }

    private fun getEventListeners(): List<WebSocketEventListener> {
        return mWebSocketAdapterEventListenersFieldReflection
            .get(mWebSocketAdapterImpl) as List<WebSocketEventListener>
    }

    private fun getWebSocket(): WebSocket? {
        return mWebSocketAdapterWebSocketFieldReflection
            .get(mWebSocketAdapterImpl)
            .let { if (it == null) null else it as WebSocket }
    }

    private fun getCurrentActionMutex(): Mutex {
        return mCurrentActionMutexFieldReflection.get(mWebSocketAdapterImpl) as Mutex
    }

    private fun getReconnectionCounter(): Int {
        return mReconnectionCounterFieldReflection.get(mWebSocketAdapterImpl) as Int
    }
}