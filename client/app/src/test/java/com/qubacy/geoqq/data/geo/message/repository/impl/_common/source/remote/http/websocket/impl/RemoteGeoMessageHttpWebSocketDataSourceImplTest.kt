package com.qubacy.geoqq.data.geo.message.repository.impl._common.source.remote.http.websocket.impl

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock.EventJsonAdapterMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.message.RemoteHttpWebSocketMessageDataSourceTest
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.location.GeoLocationActionPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.message.GeoMessageActionPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.type.GeoMessageActionType
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.GeoMessageAddedEventPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket.impl.RemoteGeoMessageHttpWebSocketDataSourceImpl
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class RemoteGeoMessageHttpWebSocketDataSourceImplTest(

) : RemoteHttpWebSocketMessageDataSourceTest<RemoteGeoMessageHttpWebSocketDataSourceImpl>() {
    private lateinit var mEventJsonAdapterMockContainer: EventJsonAdapterMockContainer
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    private var mGeoLocationActionPayloadJsonAdapterToJson: String? = null

    private var mGeoLocationActionPayloadJsonAdapterToJsonCallFlag = false

    private var mGeoMessageActionPayloadJsonAdapterToJson: String? = null

    private var mGeoMessageActionPayloadJsonAdapterToJsonCallFlag = false

    override fun setup() {
        super.setup()

        mEventJsonAdapterMockContainer = EventJsonAdapterMockContainer()
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()

        val geoMessageActionPayloadJsonAdapterMock = mockGeoMessageActionPayloadJsonAdapter()
        val geoLocationActionPayloadJsonAdapterMock = mockGeoLocationActionPayloadJsonAdapter()
        val geoMessageAddedEventPayloadJsonAdapterMock = mockGeoMessageAddedEventPayloadJsonAdapter()

        mWebSocketDataSource = RemoteGeoMessageHttpWebSocketDataSourceImpl(
            mEventJsonAdapter = mEventJsonAdapterMockContainer.eventJsonAdapterMock,
            mErrorDataSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            webSocketAdapter = mWebSocketAdapterMock,
            mGeoMessageActionPayloadJsonAdapter = geoMessageActionPayloadJsonAdapterMock,
            mGeoLocationActionPayloadJsonAdapter = geoLocationActionPayloadJsonAdapterMock,
            mGeoMessageAddedEventPayloadJsonAdapter = geoMessageAddedEventPayloadJsonAdapterMock
        )
    }

    override fun clear() {
        super.clear()

        mGeoLocationActionPayloadJsonAdapterToJson = null

        mGeoLocationActionPayloadJsonAdapterToJsonCallFlag = false

        mGeoMessageActionPayloadJsonAdapterToJson = null

        mGeoMessageActionPayloadJsonAdapterToJsonCallFlag = false
    }

    private fun mockGeoMessageActionPayloadJsonAdapter(): JsonAdapter<GeoMessageActionPayload> {
        val geoMessageActionPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)
            as JsonAdapter<GeoMessageActionPayload>

        Mockito.`when`(geoMessageActionPayloadJsonAdapterMock.toJson(
            AnyMockUtil.anyObject<GeoMessageActionPayload>()
        )).thenAnswer {
            mGeoMessageActionPayloadJsonAdapterToJsonCallFlag = true
            mGeoMessageActionPayloadJsonAdapterToJson
        }

        return geoMessageActionPayloadJsonAdapterMock
    }

    private fun mockGeoLocationActionPayloadJsonAdapter(): JsonAdapter<GeoLocationActionPayload> {
        val geoLocationActionPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)
            as JsonAdapter<GeoLocationActionPayload>

        Mockito.`when`(geoLocationActionPayloadJsonAdapterMock.toJson(
            AnyMockUtil.anyObject<GeoLocationActionPayload>()
        )).thenAnswer {
            mGeoLocationActionPayloadJsonAdapterToJsonCallFlag = true
            mGeoLocationActionPayloadJsonAdapterToJson
        }

        return geoLocationActionPayloadJsonAdapterMock
    }

    private fun mockGeoMessageAddedEventPayloadJsonAdapter(): JsonAdapter<GeoMessageAddedEventPayload> {
        val geoMessageAddedEventPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)

        return geoMessageAddedEventPayloadJsonAdapterMock as JsonAdapter<GeoMessageAddedEventPayload>
    }

    @Test
    fun sendMessageTest() {
        val text = "test"
        val latitude = 0f
        val longitude = 0f

        val type = GeoMessageActionType.ADD_GEO_MESSAGE.title
        val payloadString = "payload"

        val expectedAction = PackagedAction(type, payloadString)

        mGeoMessageActionPayloadJsonAdapterToJson = payloadString

        mWebSocketDataSource.sendMessage(text, latitude, longitude)

        val gottenAction = mWebSocketAdapterSendAction!!

        Assert.assertTrue(mGeoMessageActionPayloadJsonAdapterToJsonCallFlag)
        Assert.assertEquals(expectedAction, gottenAction)
    }

    @Test
    fun sendLocationTest() {
        val latitude = 0f
        val longitude = 0f
        val radius = 0

        val type = GeoMessageActionType.UPDATE_USER_LOCATION.title
        val payloadString = "payload"

        val expectedAction = PackagedAction(type, payloadString)

        mGeoLocationActionPayloadJsonAdapterToJson = payloadString

        mWebSocketDataSource.sendLocation(latitude, longitude, radius)

        val gottenAction = mWebSocketAdapterSendAction!!

        Assert.assertTrue(mGeoLocationActionPayloadJsonAdapterToJsonCallFlag)
        Assert.assertEquals(expectedAction, gottenAction)
    }

    @Test
    fun processErrorMessageEvent() = runTest {
        // todo: nothing to check for now..


    }
}