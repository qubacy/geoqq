package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock.EventJsonAdapterMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.message.RemoteHttpWebSocketMessageDataSourceTest
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.MateRequestAddedEventPayload
import com.squareup.moshi.JsonAdapter
import org.mockito.Mockito

class RemoteMateRequestHttpWebSocketDataSourceImplTest(

) : RemoteHttpWebSocketMessageDataSourceTest<RemoteMateRequestHttpWebSocketDataSourceImpl>() {
    private lateinit var mEventJsonAdapterMockContainer: EventJsonAdapterMockContainer
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    override fun setup() {
        super.setup()

        mEventJsonAdapterMockContainer = EventJsonAdapterMockContainer()
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        val mateRequestAddedEventPayloadJsonAdapterMock =
            mockMateRequestAddedEventPayloadJsonAdapter()

        mWebSocketDataSource = RemoteMateRequestHttpWebSocketDataSourceImpl(
            mEventJsonAdapter = mEventJsonAdapterMockContainer.eventJsonAdapterMock,
            mErrorDataSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            webSocketAdapter = mWebSocketAdapterMock,
            mMateRequestAddedEventPayloadJsonAdapter = mateRequestAddedEventPayloadJsonAdapterMock
        )
    }

    private fun mockMateRequestAddedEventPayloadJsonAdapter(

    ): JsonAdapter<MateRequestAddedEventPayload> {
        val mateRequestAddedEventPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)
            as JsonAdapter<MateRequestAddedEventPayload>

        return mateRequestAddedEventPayloadJsonAdapterMock
    }
}