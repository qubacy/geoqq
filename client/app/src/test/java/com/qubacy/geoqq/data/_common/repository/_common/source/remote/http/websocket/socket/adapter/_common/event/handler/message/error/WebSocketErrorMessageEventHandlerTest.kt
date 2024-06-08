package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.message.error

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.model.error.auth.AuthErrorType
import com.qubacy.geoqq._common.model.error.general.GeneralErrorType
import com.qubacy.geoqq._common.util.json.adapter.extension.skipObject
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.error.type.DataHttpWebSocketErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock.EventJsonAdapterMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.ErrorEventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter.ErrorEventPayloadJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error.WebSocketErrorMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error.callback.WebSocketErrorMessageEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.WebSocketMessageEvent
import com.qubacy.geoqq.data._common.repository.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.squareup.moshi.JsonReader
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class WebSocketErrorMessageEventHandlerTest {
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mEventJsonAdapterMockContainer: EventJsonAdapterMockContainer

    private lateinit var mWebSocketErrorMessageEventHandler: WebSocketErrorMessageEventHandler

    private var mWebSocketErrorMessageEventHandlerCallbackShutdownWebSocketWithErrorCallFlag = false
    private var mWebSocketErrorMessageEventHandlerCallbackRetryActionSendingCallFlag = false

    private var mErrorEventPayloadJsonAdapterFromJson: ErrorEventPayload? = null

    private var mErrorEventPayloadJsonAdapterFromJsonCallFlag = false

    @Before
    fun setup() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mEventJsonAdapterMockContainer = EventJsonAdapterMockContainer()

        val errorEventPayloadJsonAdapterMock = mockErrorEventPayloadJsonAdapter()
        val webSocketErrorMessageEventHandlerCallback =
            mockWebSocketErrorMessageEventHandlerCallback()

        mWebSocketErrorMessageEventHandler = WebSocketErrorMessageEventHandler(
            mErrorDataSourceMockContainer.errorDataSourceMock,
            mTokenDataRepositoryMockContainer.tokenDataRepository,
            mEventJsonAdapterMockContainer.eventJsonAdapterMock,
            errorEventPayloadJsonAdapterMock
        ).apply {
            setCallback(webSocketErrorMessageEventHandlerCallback)
        }
    }

    private fun mockErrorEventPayloadJsonAdapter(): ErrorEventPayloadJsonAdapter {
        val errorEventPayloadJsonAdapterMock = Mockito.mock(ErrorEventPayloadJsonAdapter::class.java)

        Mockito.`when`(errorEventPayloadJsonAdapterMock.fromJson(
            AnyMockUtil.anyObject<JsonReader>()
        )).thenAnswer {
            mErrorEventPayloadJsonAdapterFromJsonCallFlag = true

            val reader = it.arguments[0] as JsonReader

            skipObject(reader)

            mErrorEventPayloadJsonAdapterFromJson!!
        }

        return errorEventPayloadJsonAdapterMock
    }

    private fun mockWebSocketErrorMessageEventHandlerCallback(

    ): WebSocketErrorMessageEventHandlerCallback {
        val webSocketErrorMessageEventHandlerCallback =
            Mockito.mock(WebSocketErrorMessageEventHandlerCallback::class.java)

        Mockito.`when`(webSocketErrorMessageEventHandlerCallback.shutdownWebSocketWithError(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mWebSocketErrorMessageEventHandlerCallbackShutdownWebSocketWithErrorCallFlag = true

            Unit
        }
        Mockito.`when`(webSocketErrorMessageEventHandlerCallback.retryActionSending()).thenAnswer {
            mWebSocketErrorMessageEventHandlerCallbackRetryActionSendingCallFlag = true

            Unit
        }

        return webSocketErrorMessageEventHandlerCallback
    }

    @After
    fun clear() {
        mWebSocketErrorMessageEventHandlerCallbackShutdownWebSocketWithErrorCallFlag = false
        mWebSocketErrorMessageEventHandlerCallbackRetryActionSendingCallFlag = false

        mErrorEventPayloadJsonAdapterFromJson = null

        mErrorEventPayloadJsonAdapterFromJsonCallFlag = false
    }

    @Test
    fun handleServerSideErrorTest() {
        val error = Error(
            DataHttpWebSocketErrorType.ACTION_FAILED_SERVER_SIDE.getErrorCode(),
            String(),
            false
        )
        val errorHttpCode = WebSocketErrorMessageEventHandler.SERVER_SIDE_ERROR_CODE

        val errorResponseContent = ErrorResponseContent(error.id)
        val errorEventPayload = ErrorEventPayload(errorHttpCode, errorResponseContent)

        val event = WebSocketMessageEvent(
            EventJsonAdapterMockContainer.EVENT_JSON_TEMPLATE.format(
                WebSocketErrorMessageEventHandler.ERROR_EVENT_TYPE_NAME, "{}"
            )
        )

        val expectedException = ErrorAppException(error)

        mErrorDataSourceMockContainer.getError = error
        mErrorEventPayloadJsonAdapterFromJson = errorEventPayload

        try {
            mWebSocketErrorMessageEventHandler.handle(event)

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mErrorEventPayloadJsonAdapterFromJsonCallFlag)
            Assert.assertEquals(expectedException.error.id, e.error.id)

            return
        }

        throw IllegalStateException()
    }

    @Test
    fun handleInvalidAccessTokenErrorTest() {
        val error = Error(
            GeneralErrorType.INVALID_ACCESS_TOKEN.getErrorCode(),
            String(),
            false
        )
        val errorHttpCode = WebSocketErrorMessageEventHandler.CLIENT_SIDE_ERROR_CODE

        val errorResponseContent = ErrorResponseContent(error.id)
        val errorEventPayload = ErrorEventPayload(errorHttpCode, errorResponseContent)

        val event = WebSocketMessageEvent(
            EventJsonAdapterMockContainer.EVENT_JSON_TEMPLATE.format(
                WebSocketErrorMessageEventHandler.ERROR_EVENT_TYPE_NAME, "{}"
            )
        )

        mErrorDataSourceMockContainer.getError = error
        mErrorEventPayloadJsonAdapterFromJson = errorEventPayload

        mWebSocketErrorMessageEventHandler.handle(event)

        Assert.assertTrue(mErrorEventPayloadJsonAdapterFromJsonCallFlag)
        Assert.assertTrue(mTokenDataRepositoryMockContainer.updateTokensCallFlag)
        Assert.assertTrue(mWebSocketErrorMessageEventHandlerCallbackRetryActionSendingCallFlag)
    }

    @Test
    fun handleInvalidRefreshTokenErrorTest() {
        val accessTokenError = Error(
            GeneralErrorType.INVALID_ACCESS_TOKEN.getErrorCode(),
            String(),
            false
        )
        val refreshTokenError = accessTokenError
            .copy(id = AuthErrorType.INVALID_REFRESH_TOKEN.getErrorCode())

        val errorHttpCode = WebSocketErrorMessageEventHandler.CLIENT_SIDE_ERROR_CODE

        val errorResponseContent = ErrorResponseContent(accessTokenError.id)
        val errorEventPayload = ErrorEventPayload(errorHttpCode, errorResponseContent)

        val event = WebSocketMessageEvent(
            EventJsonAdapterMockContainer.EVENT_JSON_TEMPLATE.format(
                WebSocketErrorMessageEventHandler.ERROR_EVENT_TYPE_NAME, "{}"
            )
        )

        mErrorDataSourceMockContainer.getError = accessTokenError
        mErrorEventPayloadJsonAdapterFromJson = errorEventPayload
        mTokenDataRepositoryMockContainer.onUpdateTokensAction = {
            throw ErrorAppException(refreshTokenError)
        }

        mWebSocketErrorMessageEventHandler.handle(event)

        Assert.assertTrue(mErrorEventPayloadJsonAdapterFromJsonCallFlag)
        Assert.assertTrue(mTokenDataRepositoryMockContainer.updateTokensCallFlag)
        Assert.assertTrue(mWebSocketErrorMessageEventHandlerCallbackShutdownWebSocketWithErrorCallFlag)
    }
}