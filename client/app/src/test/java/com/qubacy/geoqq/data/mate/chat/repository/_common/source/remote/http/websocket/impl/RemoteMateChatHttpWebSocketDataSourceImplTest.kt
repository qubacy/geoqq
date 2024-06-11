package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock.EventJsonAdapterMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.message.RemoteHttpWebSocketMessageDataSourceTest
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.MateChatEventPayload
import com.squareup.moshi.JsonAdapter
import org.mockito.Mockito

class RemoteMateChatHttpWebSocketDataSourceImplTest(

) : RemoteHttpWebSocketMessageDataSourceTest<RemoteMateChatHttpWebSocketDataSourceImpl>() {
    private lateinit var mEventJsonAdapterMockContainer: EventJsonAdapterMockContainer
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    override fun setup() {
        super.setup()

        mEventJsonAdapterMockContainer = EventJsonAdapterMockContainer()
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        val mateChatEventPayloadJsonAdapterMock = mockMateChatEventPayloadJsonAdapter()

        mWebSocketDataSource = RemoteMateChatHttpWebSocketDataSourceImpl(
            mEventJsonAdapter = mEventJsonAdapterMockContainer.eventJsonAdapterMock,
            mErrorDataSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            webSocketAdapter = mWebSocketAdapterMock,
            mMateChatEventPayloadJsonAdapter = mateChatEventPayloadJsonAdapterMock
        )
    }

    private fun mockMateChatEventPayloadJsonAdapter(): JsonAdapter<MateChatEventPayload> {
        val mateChatEventPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)
            as JsonAdapter<MateChatEventPayload>

        return mateChatEventPayloadJsonAdapterMock
    }
}