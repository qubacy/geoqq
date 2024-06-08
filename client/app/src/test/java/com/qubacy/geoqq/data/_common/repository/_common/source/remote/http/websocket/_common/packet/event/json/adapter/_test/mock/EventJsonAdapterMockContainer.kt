package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter._test.mock

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.util.json.adapter.extension.skipObject
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.callback.EventJsonAdapterCallback
import com.squareup.moshi.JsonAdapter
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

    private var mMockEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag = false
    val mockEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag get() =
        mMockEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag

    init {
        val eventJsonAdapterCallbackMock = mockEventJsonAdapterCallback()

        eventJsonAdapterMock = EventJsonAdapter().apply {
            setCallback(eventJsonAdapterCallbackMock)
        }
    }

    fun clear() {
        mMockEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag = false

        packetPayload = null
    }

    private fun mockEventJsonAdapterCallback(): EventJsonAdapterCallback {
        val eventJsonAdapterCallbackMock = Mockito.mock(EventJsonAdapterCallback::class.java)

        Mockito.`when`(eventJsonAdapterCallbackMock.getEventPayloadJsonAdapterByType(
            Mockito.anyString()
        )).thenAnswer {
            mMockEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag = true

            mockPacketPayloadJsonAdapter()
        }

        return eventJsonAdapterCallbackMock
    }

    private fun mockPacketPayloadJsonAdapter(): JsonAdapter<*>? {
        if (packetPayload == null) return null

        val packetPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)

        Mockito.`when`(packetPayloadJsonAdapterMock.fromJson(
            AnyMockUtil.anyObject<JsonReader>()
        )).thenAnswer {
            val reader = it.arguments[0] as JsonReader

            skipObject(reader)

            packetPayload!!
        }

        return packetPayloadJsonAdapterMock
    }
}