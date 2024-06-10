package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket.impl

import app.cash.turbine.test
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.RemoteHttpWebSocketDataSourceTest
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class RemoteAuthHttpWebSocketDataSourceImplTest(

) : RemoteHttpWebSocketDataSourceTest<RemoteAuthHttpWebSocketDataSourceImpl>() {
    override fun setup() {
        super.setup()

        mWebSocketDataSource = RemoteAuthHttpWebSocketDataSourceImpl().apply {
            setWebSocketAdapter(mWebSocketAdapterMock)
        }
    }

    @Test
    fun processErrorEventTest() = runTest {
        val expectedError = TestError.normal

        mWebSocketDataSource.eventFlow.test {
            mWebSocketDataSource.onEventGotten(WebSocketErrorEvent(expectedError))

            val result = awaitItem()

            Assert.assertEquals(WebSocketErrorResult::class, result::class)

            result as WebSocketErrorResult

            Assert.assertEquals(expectedError, result.error)
        }
    }
}