package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._test.mock

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import org.mockito.Mockito

class WebSocketAdapterMockAdapter {
    val webSocketAdapter: WebSocketAdapter

    var isOpen = false

    private var mIsOpenCallFlag = false
    val isOpenCallFlag get() = mIsOpenCallFlag

    private var mAddEventListenerCallFlag = false
    val addEventListenerCallFlag get() = mAddEventListenerCallFlag

    private var mRemoveEventListenerCallFlag = false
    val remoteEventListenerCallFlag get() = mRemoveEventListenerCallFlag

    private var mOpenCallFlag = false
    val openCallFlag get() = mOpenCallFlag

    private var mCloseCallFlag = false
    val closeCallFlag get() = mCloseCallFlag

    private var mSendCallFlag = false
    val sendCallFlag get() = mSendCallFlag

    init {
        webSocketAdapter = mockWebSocketAdapter()
    }

    fun clear() {
        mIsOpenCallFlag = false
        mAddEventListenerCallFlag = false
        mRemoveEventListenerCallFlag = false
        mOpenCallFlag = false
        mCloseCallFlag = false
        mSendCallFlag = false
    }

    private fun mockWebSocketAdapter(): WebSocketAdapter {
        val webSocketAdapter = Mockito.mock(WebSocketAdapter::class.java)

        Mockito.`when`(webSocketAdapter.addEventListener(AnyMockUtil.anyObject())).thenAnswer {
            mAddEventListenerCallFlag = true

            Unit
        }
        Mockito.`when`(webSocketAdapter.removeEventListener(AnyMockUtil.anyObject())).thenAnswer {
            mRemoveEventListenerCallFlag = true

            Unit
        }
        Mockito.`when`(webSocketAdapter.open()).thenAnswer {
            mOpenCallFlag = true

            Unit
        }
        Mockito.`when`(webSocketAdapter.isOpen()).thenAnswer {
            mIsOpenCallFlag = true
            isOpen
        }
        Mockito.`when`(webSocketAdapter.close()).thenAnswer {
            mCloseCallFlag = true

            Unit
        }
        Mockito.`when`(webSocketAdapter.sendAction(AnyMockUtil.anyObject())).thenAnswer {
            mSendCallFlag = true

            Unit
        }

        return webSocketAdapter
    }
}