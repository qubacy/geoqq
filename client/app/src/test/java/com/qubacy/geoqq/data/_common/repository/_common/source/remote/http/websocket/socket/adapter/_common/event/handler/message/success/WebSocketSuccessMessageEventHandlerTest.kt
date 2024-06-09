package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.message.success

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.Event
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.header.EventHeader
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock.EventJsonAdapterMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.success.SuccessEventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.success.json.adapter.SuccessEventPayloadJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.success.WebSocketSuccessMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.success.callback.WebSocketSuccessMessageEventHndlrClbck
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.domain.WebSocketDomainMessageEvent
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class WebSocketSuccessMessageEventHandlerTest {
    private lateinit var mEventJsonAdapterMockContainer: EventJsonAdapterMockContainer

    private lateinit var mWebSocketSuccessMessageEventHandler: WebSocketSuccessMessageEventHandler

    private var mWebSocketSuccessMessageEventHndlrClbckOnWebSocketMessageSucceededCallFlag = false

    @Before
    fun setup() {
        mEventJsonAdapterMockContainer = EventJsonAdapterMockContainer()

        val successEventPayloadJsonAdapterMock = mockWebSocketSuccessMessageEventHandler()
        val webSocketSuccessMessageEventHndlrClbckMock = mockWebSocketSuccessMessageEventHndlrClbck()

        mWebSocketSuccessMessageEventHandler = WebSocketSuccessMessageEventHandler(
            mEventJsonAdapterMockContainer.eventJsonAdapterMock,
            successEventPayloadJsonAdapterMock
        ).apply {
            setCallback(webSocketSuccessMessageEventHndlrClbckMock)
        }
    }

    private fun mockWebSocketSuccessMessageEventHandler(): SuccessEventPayloadJsonAdapter {
        val successEventPayloadJsonAdapterMock =
            Mockito.mock(SuccessEventPayloadJsonAdapter::class.java)

        return successEventPayloadJsonAdapterMock
    }

    private fun mockWebSocketSuccessMessageEventHndlrClbck(): WebSocketSuccessMessageEventHndlrClbck {
        val webSocketSuccessMessageEventHndlrClbckMock =
            Mockito.mock(WebSocketSuccessMessageEventHndlrClbck::class.java)

        Mockito.`when`(
            webSocketSuccessMessageEventHndlrClbckMock.onWebSocketMessageSucceeded()
        ).thenAnswer {
            mWebSocketSuccessMessageEventHndlrClbckOnWebSocketMessageSucceededCallFlag = true

            Unit
        }

        return webSocketSuccessMessageEventHndlrClbckMock
    }

    @After
    fun clear() {
        mWebSocketSuccessMessageEventHndlrClbckOnWebSocketMessageSucceededCallFlag = false
    }

    @Test
    fun handleTest() {
        val webSocketEvent = WebSocketDomainMessageEvent(
            EventJsonAdapterMockContainer.EVENT_JSON_TEMPLATE.format(
                WebSocketSuccessMessageEventHandler.SUCCESS_POSTFIX, "{}"
            )
        )
        val event = Event(
            EventHeader(WebSocketSuccessMessageEventHandler.SUCCESS_POSTFIX),
            SuccessEventPayload()
        )

        mEventJsonAdapterMockContainer.eventJsonAdapterFromJson = event

        mWebSocketSuccessMessageEventHandler.handle(webSocketEvent)

        Assert.assertTrue(mEventJsonAdapterMockContainer.eventJsonAdapterFromJsonCallFlag)
        Assert.assertTrue(mWebSocketSuccessMessageEventHndlrClbckOnWebSocketMessageSucceededCallFlag)
    }
}