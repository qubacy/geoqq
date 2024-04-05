package com.qubacy.geoqq.data._common.util.http.executor

import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.util.http.executor._test.mock.OkHttpClientMockContainer
import com.qubacy.geoqq.data.error.repository._test.mock.ErrorDataRepositoryMockContainer
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
    }

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mOkHttpClientMockContainer: OkHttpClientMockContainer
    private lateinit var mCallMock: Call<TestResponseBody>

    private var mRequestFails: Boolean? = null
    private var mErrorBody: ResponseBody? = null
    private var mResponseBody: TestResponseBody? = null

    private var mCancelAllCallFlag = false
    private var mResponseBodyCallFlag = false
    private var mResponseBodyErrorBodyCallFlag = false
    private var mCallExecuteCallFlag = false

    @Before
    fun setup() {
        initExecuteNetworkRequestContext()
    }

    @After
    fun clear() {
        mRequestFails = null
        mErrorBody = null
        mResponseBody = null

        mCancelAllCallFlag = false
        mResponseBodyCallFlag = false
        mResponseBodyErrorBodyCallFlag = false
        mCallExecuteCallFlag = false
    }

    private fun initExecuteNetworkRequestContext() {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mOkHttpClientMockContainer = OkHttpClientMockContainer()

        initCallMock()
    }

    private fun initCallMock() {
        val responseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(responseMock.body()).thenAnswer {
            mResponseBodyCallFlag = true
            mResponseBody
        }
        Mockito.`when`(responseMock.errorBody()).thenAnswer {
            mResponseBodyErrorBodyCallFlag = true
            mErrorBody
        }

        val callMock = Mockito.mock(Call::class.java)

        Mockito.`when`(callMock.execute()).thenAnswer {
            mCallExecuteCallFlag = true

            if (mRequestFails == true) throw IllegalStateException()

            responseMock
        }

        mCallMock = callMock as Call<TestResponseBody>
    }

    @Test
    fun executeNetworkRequestWhenRequestFailsTest() {
        val expectedError = DEFAULT_ERROR

        mRequestFails = true
        mErrorDataRepositoryMockContainer.getError = expectedError

        try {
            executeNetworkRequest(
                mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
                mOkHttpClientMockContainer.httpClient,
                mCallMock
            )

            throw IllegalStateException()

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mCallExecuteCallFlag)
            Assert.assertEquals(expectedError, e.error)
        }
    }

    @Test
    fun executeNetworkRequestWhenErrorResponseTest() {
        val errorBody = TestErrorResponseBody()

        val expectedError = DEFAULT_ERROR

        mErrorBody = errorBody
        mErrorDataRepositoryMockContainer.getError = expectedError

        try {
            executeNetworkRequest(
                mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
                mOkHttpClientMockContainer.httpClient,
                mCallMock
            )

            throw IllegalStateException()

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mCallExecuteCallFlag)
            Assert.assertTrue(mResponseBodyErrorBodyCallFlag)
            Assert.assertEquals(expectedError, e.error)
        }
    }

    @Test
    fun executeNetworkRequestWhenSuccessfulResponseTest() {
        val expectedResponseBody = TestResponseBody()

        mResponseBody = expectedResponseBody

        val gottenResponseBody = executeNetworkRequest(
            mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mOkHttpClientMockContainer.httpClient,
            mCallMock
        )

        Assert.assertTrue(mCallExecuteCallFlag)
        Assert.assertTrue(mResponseBodyErrorBodyCallFlag)
        Assert.assertTrue(mResponseBodyCallFlag)
        Assert.assertEquals(expectedResponseBody, gottenResponseBody)
    }
}