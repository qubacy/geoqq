package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common

import androidx.annotation.CallSuper
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

abstract class RemoteHttpWebSocketDataSourceTest<SourceType : RemoteHttpWebSocketDataSource> {
    protected lateinit var mWebSocketAdapterMock: WebSocketAdapter

    private lateinit var mIsStartedFieldReflection: Field

    protected lateinit var mWebSocketDataSource: SourceType

    protected var mWebSocketAdapterAddEventListenerCallFlag = false
    protected var mWebSocketAdapterRemoveEventListenerCallFlag = false

    @Before
    @CallSuper
    open fun setup() {
        mIsStartedFieldReflection = RemoteHttpWebSocketDataSource::class.java
            .getDeclaredField("mIsStarted").apply { isAccessible = true }

        mWebSocketAdapterMock = mockWebSocketAdapter()
    }

    @After
    @CallSuper
    open fun clear() {
        mWebSocketAdapterAddEventListenerCallFlag = false
        mWebSocketAdapterRemoveEventListenerCallFlag = false
    }

    protected open fun mockWebSocketAdapter(): WebSocketAdapter {
        val webSocketAdapter = Mockito.mock(WebSocketAdapter::class.java)

        Mockito.`when`(webSocketAdapter.addEventListener(AnyMockUtil.anyObject())).then {
            mWebSocketAdapterAddEventListenerCallFlag = true

            Unit
        }
        Mockito.`when`(webSocketAdapter.removeEventListener(AnyMockUtil.anyObject())).then {
            mWebSocketAdapterRemoveEventListenerCallFlag = true

            Unit
        }

        return webSocketAdapter
    }

    @Test
    open fun startProducingTest() {
        val expectedIsStarted = true

        mWebSocketDataSource.startProducing()

        val gottenIsStarted = getIsStarted()

        Assert.assertTrue(mWebSocketAdapterAddEventListenerCallFlag)
        Assert.assertEquals(expectedIsStarted, gottenIsStarted)
    }

    @Test
    open fun stopProducingTest() {
        val expectedIsStarted = false

        mWebSocketDataSource.startProducing()
        mWebSocketDataSource.stopProducing()

        val gottenIsStarted = getIsStarted()

        Assert.assertTrue(mWebSocketAdapterRemoveEventListenerCallFlag)
        Assert.assertEquals(expectedIsStarted, gottenIsStarted)
    }

    protected fun getIsStarted(): Boolean {
        return mIsStartedFieldReflection.getBoolean(mWebSocketDataSource)
    }
}