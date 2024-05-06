package com.qubacy.geoqq.data._common.repository.source.remote.http.executor

import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response

class HttpExecutorUtilTest {
    class TestResponseBody()
    class TestErrorResponseBody() : ResponseBody() {
        override fun contentType(): MediaType? { return null }
        override fun contentLength(): Long { return 0 }
        override fun source(): BufferedSource { return Buffer() }
    }

    companion object {
        val DEFAULT_ERROR = TestError.normal
        val DEFAULT_ERROR_RESPONSE_CONTENT = ErrorResponseContent(DEFAULT_ERROR.id)
        val DEFAULT_ERROR_RESPONSE = ErrorResponse(DEFAULT_ERROR_RESPONSE_CONTENT)

        val DEFAULT_CLIENT_ERROR_CODE = 400
        val DEFAULT_REMOTE_ERROR_CODE = 500
    }

    private lateinit var mHttpCallExecutor: HttpCallExecutor

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataSourceMockContainer
    private lateinit var mErrorJsonAdapter: ErrorJsonAdapter

    private lateinit var mCallMock: Call<TestResponseBody>

    private var mHttpClientCancelAllCallFlag: Boolean = false

    private var mCallRequestFails: Boolean? = null
    private var mCallErrorBody: ResponseBody? = null
    private var mCallResponseBody: TestResponseBody? = null
    private var mCallResponseCode: Int? = null

    private var mCallResponseBodyCallFlag = false
    private var mCallResponseBodyErrorBodyCallFlag = false
    private var mCallExecuteCallFlag = false

    private var mRetrofitConvert: ErrorResponse? = null

    private var mRetrofitGetResponseConverterCallFlag = false
    private var mRetrofitConvertCallFlag = false

    @Before
    fun setup() {
        initExecuteNetworkRequestContext()

        mHttpCallExecutor = HttpCallExecutor(
            mErrorDataRepositoryMockContainer.errorDataSourceMock,
            mErrorJsonAdapter
        )
    }

    @After
    fun clear() {
        mHttpClientCancelAllCallFlag = false

        mCallRequestFails = null
        mCallErrorBody = null
        mCallResponseBody = null
        mCallResponseCode = null

        mCallResponseBodyCallFlag = false
        mCallResponseBodyErrorBodyCallFlag = false
        mCallExecuteCallFlag = false

        mRetrofitConvert = null

        mRetrofitGetResponseConverterCallFlag = false
        mRetrofitConvertCallFlag = false
    }

    private fun initExecuteNetworkRequestContext() {
        mErrorDataRepositoryMockContainer = ErrorDataSourceMockContainer()

        initErrorJsonAdapter()

        initCallMock()
    }

    private fun initErrorJsonAdapter() {
        mErrorJsonAdapter = ErrorJsonAdapter()
    }

    private fun initCallMock() {
        val responseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(responseMock.body()).thenAnswer {
            mCallResponseBodyCallFlag = true
            mCallResponseBody
        }
        Mockito.`when`(responseMock.errorBody()).thenAnswer {
            mCallResponseBodyErrorBodyCallFlag = true
            mCallErrorBody
        }
        Mockito.`when`(responseMock.code()).thenAnswer {
            mCallResponseCode
        }

        val callMock = Mockito.mock(Call::class.java)

        Mockito.`when`(callMock.execute()).thenAnswer {
            mCallExecuteCallFlag = true

            if (mCallRequestFails == true) throw ErrorAppException(DEFAULT_ERROR)

            responseMock
        }

        mCallMock = callMock as Call<TestResponseBody>
    }

    @Test
    fun executeNetworkRequestWhenRequestFailsTest() {
        val expectedError = DEFAULT_ERROR

        mCallRequestFails = true
        mErrorDataRepositoryMockContainer.getError = expectedError

        try {
            mHttpCallExecutor.executeNetworkRequest(mCallMock)

            throw IllegalStateException()

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mCallExecuteCallFlag)
            Assert.assertEquals(expectedError, e.error)
        }
    }

    @Test
    fun executeNetworkRequestWhenClientErrorResponseTest() {
        val errorBody = TestErrorResponseBody()

        val expectedError = DEFAULT_ERROR

        mCallErrorBody = errorBody
        mCallResponseCode = DEFAULT_CLIENT_ERROR_CODE
        mRetrofitConvert = DEFAULT_ERROR_RESPONSE
        mErrorDataRepositoryMockContainer.getError = expectedError

        try {
            mHttpCallExecutor.executeNetworkRequest(mCallMock)

            throw IllegalStateException()

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mCallExecuteCallFlag)
            Assert.assertTrue(mCallResponseBodyErrorBodyCallFlag)
            Assert.assertTrue(mRetrofitGetResponseConverterCallFlag)
            Assert.assertTrue(mRetrofitConvertCallFlag)

            Assert.assertEquals(expectedError, e.error)
        }
    }

    @Test
    fun executeNetworkRequestWhenRemoteErrorResponseTest() {
        val errorBody = TestErrorResponseBody()

        val expectedError = DEFAULT_ERROR

        mCallErrorBody = errorBody
        mCallResponseCode = DEFAULT_REMOTE_ERROR_CODE
        mErrorDataRepositoryMockContainer.getError = expectedError

        try {
            mHttpCallExecutor.executeNetworkRequest(mCallMock)

            throw IllegalStateException()

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mCallExecuteCallFlag)
            Assert.assertTrue(mCallResponseBodyErrorBodyCallFlag)
            Assert.assertFalse(mRetrofitGetResponseConverterCallFlag)
            Assert.assertFalse(mRetrofitConvertCallFlag)

            Assert.assertEquals(expectedError, e.error)
        }
    }

    @Test
    fun executeNetworkRequestWhenSuccessfulResponseTest() {
        val expectedResponseBody = TestResponseBody()

        mCallResponseBody = expectedResponseBody

        val gottenResponseBody = mHttpCallExecutor.executeNetworkRequest(mCallMock)

        Assert.assertTrue(mCallExecuteCallFlag)
        Assert.assertTrue(mCallResponseBodyErrorBodyCallFlag)
        Assert.assertTrue(mCallResponseBodyCallFlag)
        Assert.assertEquals(expectedResponseBody, gottenResponseBody)
    }
}