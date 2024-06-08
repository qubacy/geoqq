package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter.ErrorResponseContentJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.ErrorEventPayload
import com.squareup.moshi.JsonReader
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ErrorEventPayloadJsonAdapterTest {
    private lateinit var mErrorEventPayloadJsonAdapter: ErrorEventPayloadJsonAdapter

    private var mErrorResponseContentJsonAdapterFromJson: ErrorResponseContent? = null

    private var mErrorResponseContentJsonAdapterFromJsonCallFlag = false

    @Before
    fun setup() {
        val errorResponseContentJsonAdapterMock = mockErrorResponseContentJsonAdapter()

        mErrorEventPayloadJsonAdapter = ErrorEventPayloadJsonAdapter(
            errorResponseContentJsonAdapterMock)
    }

    private fun mockErrorResponseContentJsonAdapter(): ErrorResponseContentJsonAdapter {
        val errorResponseContentJsonAdapterMock =
            Mockito.mock(ErrorResponseContentJsonAdapter::class.java)

        Mockito.`when`(errorResponseContentJsonAdapterMock.fromJson(
            Mockito.any(JsonReader::class.java)
        )).thenAnswer {
            mErrorResponseContentJsonAdapterFromJsonCallFlag = true
            mErrorResponseContentJsonAdapterFromJson
        }

        return errorResponseContentJsonAdapterMock
    }

    @After
    fun clear() {
        mErrorResponseContentJsonAdapterFromJson = null

        mErrorResponseContentJsonAdapterFromJsonCallFlag = false
    }

    @Test
    fun fromJsonTest() {
        class TestCase(
            val errorEventPayloadJson: String,
            val errorEventPayloadContent: ErrorResponseContent?,
            val expectedErrorEventPayload: ErrorEventPayload?
        )

        val errorEventPayloadJsonTemplate =
            "{\"code\": \"%d\",\n" +
                "\"error\": {\n" +
                    "\"id\": \"%d\"" +
                "}" +
            "}"

        val errorResponseContent = ErrorResponseContent(1000)

        val testCases = listOf(
            TestCase(
                errorEventPayloadJsonTemplate.format(),
                null,
                null
            ),
            TestCase(
                errorEventPayloadJsonTemplate.format(),
                errorResponseContent,
                ErrorEventPayload(errorResponseContent.id, errorResponseContent)
            )
        )

        for (testCase in testCases) {
            setup()

            mErrorResponseContentJsonAdapterFromJson = testCase.errorEventPayloadContent

            val gottenErrorEventPayload =
                mErrorEventPayloadJsonAdapter.fromJson(testCase.errorEventPayloadJson)

            Assert.assertTrue(mErrorResponseContentJsonAdapterFromJsonCallFlag)
            Assert.assertEquals(testCase.expectedErrorEventPayload, gottenErrorEventPayload)

            clear()
        }
    }
}