package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock.EventJsonAdapterMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.message.RemoteHttpWebSocketMessageDataSourceTest
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.payload.add.AddMateMessageActionPayload
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.type.MateMessageActionType
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.MateMessageAddedEventPayload
import com.squareup.moshi.JsonAdapter
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class RemoteMateMessageHttpWebSocketDataSourceImplTest(

) : RemoteHttpWebSocketMessageDataSourceTest<RemoteMateMessageHttpWebSocketDataSourceImpl>() {
    private lateinit var mEventJsonAdapterMockContainer: EventJsonAdapterMockContainer
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    private var mAddMateMessageActionPayloadJsonAdapterToJson: String? = null

    private var mAddMateMessageActionPayloadJsonAdapterToJsonCallFlag = false

    override fun setup() {
        super.setup()

        mEventJsonAdapterMockContainer = EventJsonAdapterMockContainer()
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        val mateMessageEventPayloadJsonAdapterMock = mockMateMessageEventPayloadJsonAdapter()
        val addMateMessageActionPayloadJsonAdapterMock = mockAddMateMessageActionPayloadJsonAdapter()

        mWebSocketDataSource = RemoteMateMessageHttpWebSocketDataSourceImpl(
            mEventJsonAdapter = mEventJsonAdapterMockContainer.eventJsonAdapterMock,
            mErrorDataSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            webSocketAdapter = mWebSocketAdapterMock,
            mMateMessageEventPayloadJsonAdapter = mateMessageEventPayloadJsonAdapterMock,
            mAddMateMessageActionPayloadJsonAdapter = addMateMessageActionPayloadJsonAdapterMock
        )
    }

    override fun clear() {
        super.clear()

        mAddMateMessageActionPayloadJsonAdapterToJson = null

        mAddMateMessageActionPayloadJsonAdapterToJsonCallFlag = false
    }

    private fun mockMateMessageEventPayloadJsonAdapter(

    ): JsonAdapter<MateMessageAddedEventPayload> {
        val mateMessageEventPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)
            as JsonAdapter<MateMessageAddedEventPayload>

        return mateMessageEventPayloadJsonAdapterMock
    }

    private fun mockAddMateMessageActionPayloadJsonAdapter(

    ): JsonAdapter<AddMateMessageActionPayload> {
        val addMateMessageActionPayloadJsonAdapter = Mockito.mock(JsonAdapter::class.java)
            as JsonAdapter<AddMateMessageActionPayload>

        Mockito.`when`(addMateMessageActionPayloadJsonAdapter.toJson(
            AnyMockUtil.anyObject<AddMateMessageActionPayload>()
        )).thenAnswer {
            mAddMateMessageActionPayloadJsonAdapterToJsonCallFlag = true
            mAddMateMessageActionPayloadJsonAdapterToJson
        }

        return addMateMessageActionPayloadJsonAdapter
    }

    @Test
    fun sendMessageTest() {
        val chatId = 0L
        val text = "test"

        val type = MateMessageActionType.ADD_MATE_MESSAGE.title
        val payloadString = "payload"

        val expectedAction = PackagedAction(type, payloadString)

        mAddMateMessageActionPayloadJsonAdapterToJson = payloadString

        mWebSocketDataSource.sendMessage(chatId, text)

        val gottenAction = mWebSocketAdapterSendAction!!

        Assert.assertTrue(mAddMateMessageActionPayloadJsonAdapterToJsonCallFlag)
        Assert.assertEquals(expectedAction, gottenAction)
    }
}