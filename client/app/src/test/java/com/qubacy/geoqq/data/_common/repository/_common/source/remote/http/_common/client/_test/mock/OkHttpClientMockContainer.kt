package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client._test.mock

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import org.mockito.Mockito

class OkHttpClientMockContainer {
    val webSocketMock: WebSocket

    val okHttpClientMock: OkHttpClient

    private var mOkHttpClientNewWebSocketCallFlag = false
    val okHttpClientNewWebSocketCallFlag get() = mOkHttpClientNewWebSocketCallFlag

    private var mOkHttpClientDispatcherCancelAllCallFlag = false
    val okHttpClientDispatcherCancelAllCallFlag get() = mOkHttpClientDispatcherCancelAllCallFlag

    var webSocketSend: Boolean = true

    private var mWebSocketSendCallFlag = false
    val webSocketSendCallFlag get() = mWebSocketSendCallFlag

    init {
        webSocketMock = mockWebSocket()
        okHttpClientMock = mockOkHttpClient()
    }

    fun reset() {
        mOkHttpClientNewWebSocketCallFlag = false
        mOkHttpClientDispatcherCancelAllCallFlag = false

        webSocketSend = true

        mWebSocketSendCallFlag = false
    }

    private fun mockWebSocket(): WebSocket {
        val webSocketMock = Mockito.mock(WebSocket::class.java)

        Mockito.`when`(webSocketMock.send(String())).thenAnswer {
            mWebSocketSendCallFlag = true
            true
        }

        return webSocketMock
    }

    private fun mockOkHttpClient(): OkHttpClient {
        val dispatcherMock = Mockito.mock(Dispatcher::class.java)

        Mockito.`when`(dispatcherMock.cancelAll()).thenAnswer {
            mOkHttpClientDispatcherCancelAllCallFlag = true

            Unit
        }

        val okHttpClientMock = Mockito.mock(OkHttpClient::class.java)

        Mockito.`when`(okHttpClientMock.dispatcher()).thenAnswer {
            dispatcherMock
        }
        Mockito.`when`(okHttpClientMock.newWebSocket(
            AnyMockUtil.anyObject(), AnyMockUtil.anyObject()
        )).thenAnswer {
            mOkHttpClientNewWebSocketCallFlag = true
            webSocketMock
        }

        return okHttpClientMock
    }
}