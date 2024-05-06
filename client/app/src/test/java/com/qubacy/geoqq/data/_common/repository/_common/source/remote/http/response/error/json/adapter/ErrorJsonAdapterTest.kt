package com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponseContent
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ErrorJsonAdapterTest {
    private lateinit var mErrorJsonAdapter: ErrorJsonAdapter

    @Before
    fun setup() {
        mErrorJsonAdapter = ErrorJsonAdapter()
    }

    @Test
    fun fromJsonTest() {
        class TestCase(
            val errorJsonString: String,
            val expectedErrorResponse: ErrorResponse
        )

        val testCases = listOf(
            TestCase("{\"error\":{\"id\":1}}", ErrorResponse(ErrorResponseContent(1))),
            TestCase(
                "{\n  \"error\": {    \n\"id\":1\n  }\n}",
                ErrorResponse(ErrorResponseContent(1))
            ),
            TestCase(
                "{\"error\":{\"id\":1,\"extra\":\"some extra\"}}",
                ErrorResponse(ErrorResponseContent(1))
            )
        )

        for (testCase in testCases) {
            val gottenErrorResponse = mErrorJsonAdapter.fromJson(testCase.errorJsonString)!!

            Assert.assertEquals(testCase.expectedErrorResponse.error.id, gottenErrorResponse.error.id)
        }
    }
}