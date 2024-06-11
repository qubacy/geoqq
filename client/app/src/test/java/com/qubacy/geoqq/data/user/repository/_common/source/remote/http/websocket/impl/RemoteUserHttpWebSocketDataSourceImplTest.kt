package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock.EventJsonAdapterMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.message.RemoteHttpWebSocketMessageDataSourceTest
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.payload.updated.UserUpdatedEventPayload
import com.squareup.moshi.JsonAdapter
import org.mockito.Mockito

class RemoteUserHttpWebSocketDataSourceImplTest(

) : RemoteHttpWebSocketMessageDataSourceTest<RemoteUserHttpWebSocketDataSourceImpl>() {
    private lateinit var mEventJsonAdapterMockContainer: EventJsonAdapterMockContainer
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    override fun setup() {
        super.setup()

        mEventJsonAdapterMockContainer = EventJsonAdapterMockContainer()
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        val userUpdatedEventPayloadJsonAdapterMock = mockUserUpdatedEventPayloadJsonAdapter()

        mWebSocketDataSource = RemoteUserHttpWebSocketDataSourceImpl(
            mEventJsonAdapter = mEventJsonAdapterMockContainer.eventJsonAdapterMock,
            mErrorDataSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            webSocketAdapter = mWebSocketAdapterMock,
            mUserUpdatedEventPayloadJsonAdapter = userUpdatedEventPayloadJsonAdapterMock
        )
    }

    private fun mockUserUpdatedEventPayloadJsonAdapter(): JsonAdapter<UserUpdatedEventPayload> {
        val userUpdatedEventPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)
            as JsonAdapter<UserUpdatedEventPayload>

        return userUpdatedEventPayloadJsonAdapterMock
    }
}