package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.Event
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.squareup.moshi.JsonReader
import org.mockito.Mockito

class EventJsonAdapterMockContainer {
    companion object {
        const val EVENT_JSON_TEMPLATE =
            "{\"${EventJsonAdapter.TYPE_PROP_NAME}\":\"%s\"," +
            "\"${EventJsonAdapter.PAYLOAD_PROP_NAME}\":%s}"
    }

    val eventJsonAdapterMock: EventJsonAdapter

    var packetPayload: PacketPayload? = null

    var eventJsonAdapterFromJson: Event? = null

    private var mEventJsonAdapterFromJsonCallFlag = false
    val eventJsonAdapterFromJsonCallFlag get() = mEventJsonAdapterFromJsonCallFlag

    init {
        eventJsonAdapterMock = mockEventJsonAdapter()
    }

    fun clear() {
        eventJsonAdapterFromJson = null

        mEventJsonAdapterFromJsonCallFlag = false

        packetPayload = null
    }

    private fun mockEventJsonAdapter(): EventJsonAdapter {
        val eventJsonAdapterMock = Mockito.mock(EventJsonAdapter::class.java)

        Mockito.`when`(eventJsonAdapterMock.fromJson(
            AnyMockUtil.anyObject<JsonReader>()
        )).thenAnswer {
            mEventJsonAdapterFromJsonCallFlag = true
            eventJsonAdapterFromJson
        }
        Mockito.`when`(eventJsonAdapterMock.fromJson(
            Mockito.anyString()
        )).thenAnswer {
            mEventJsonAdapterFromJsonCallFlag = true
            eventJsonAdapterFromJson
        }

        return eventJsonAdapterMock
    }
}