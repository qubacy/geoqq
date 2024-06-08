package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter

import com.qubacy.geoqq._common.util.json.adapter.extension.skipObject
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet._common.payload.PacketPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.Event
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.header.EventHeader
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.callback.EventJsonAdapterCallback
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class EventJsonAdapterTest {
    private lateinit var mEventJsonAdapter: EventJsonAdapter

    private var mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByType: JsonAdapter<*>? = null

    private var mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag = false

    @Before
    fun setup() {
        val callback = mockEventJsonAdapterCallback()

        mEventJsonAdapter = EventJsonAdapter().apply {
            setCallback(callback)
        }
    }

    private fun mockEventJsonAdapterCallback(): EventJsonAdapterCallback {
        val eventJsonAdapterCallbackMock = Mockito.mock(EventJsonAdapterCallback::class.java)

        Mockito.`when`(eventJsonAdapterCallbackMock.getEventPayloadJsonAdapterByType(
            Mockito.anyString()
        )).thenAnswer {
            mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag = true
            mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByType
        }

        return eventJsonAdapterCallbackMock
    }

    @After
    fun clear() {
        mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByType = null

        mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag = false
    }

    @Test
    fun fromJsonTest() {
        class TestCase(
            val eventJson: String,
            val eventPayload: PacketPayload?,
            val expectedEvent: Event?
        )

        val eventJsonTemplate =
            "{\"${EventJsonAdapter.TYPE_PROP_NAME}\":\"%s\"," +
            "\"${EventJsonAdapter.PAYLOAD_PROP_NAME}\":%s}"

        val packetPayloadMock = Mockito.mock(PacketPayload::class.java)

        val testCases = listOf(
            TestCase(
                eventJsonTemplate.format("test", "{}"),
                null,
                null
            ),
            TestCase(
                eventJsonTemplate.format("test", "{}"),
                packetPayloadMock,
                Event(
                    EventHeader("test"),
                    packetPayloadMock
                )
            )
        )

        for (testCase in testCases) {
            setup()

            val eventJsonAdapter = testCase.eventPayload.let {
                if (it == null) null else mockPacketPayloadJsonAdapter(it)
            }

            mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByType = eventJsonAdapter

            val gottenEvent = mEventJsonAdapter.fromJson(testCase.eventJson)

            Assert.assertTrue(mEventJsonAdapterCallbackGetEventPayloadJsonAdapterByTypeCallFlag)
            Assert.assertEquals(testCase.expectedEvent, gottenEvent)

            clear()
        }
    }

    private fun mockPacketPayloadJsonAdapter(packetPayload: PacketPayload): JsonAdapter<*> {
        val packetPayloadJsonAdapterMock = Mockito.mock(JsonAdapter::class.java)

        Mockito.`when`(packetPayloadJsonAdapterMock.fromJson(
            Mockito.any(JsonReader::class.java)
        )).thenAnswer {
            val reader = it.arguments[0] as JsonReader

            skipObject(reader)

            packetPayload
        }

        return packetPayloadJsonAdapterMock
    }
}