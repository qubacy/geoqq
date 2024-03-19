package com.qubacy.geoqq.data._common.util.http.executor

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
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

    private lateinit var mErrorDataRepositoryMock: ErrorDataRepository
    private lateinit var mCallMock: Call<TestResponseBody>

    private var mResponseBodyCallFlag = false
    private var mResponseBodyErrorBodyCallFlag = false
    private var mCallExecuteCallFlag = false

    @Before
    fun setup() {
        initExecuteNetworkRequestContext()
    }

    @After
    fun clear() {
        mResponseBodyCallFlag = false
        mResponseBodyErrorBodyCallFlag = false
        mCallExecuteCallFlag = false
    }

    private fun initExecuteNetworkRequestContext(
        getErrorResult: Error = DEFAULT_ERROR,
        requestFails: Boolean = false,
        errorBody: ResponseBody? = null,
        body: TestResponseBody? = TestResponseBody()
    ) {
        initErrorDataRepositoryMock(getErrorResult)
        initCallMock(requestFails, errorBody, body)
    }

    private fun initErrorDataRepositoryMock(
        getErrorResult: Error
    ) {
        val errorDataRepositoryMock = Mockito.mock(ErrorDataRepository::class.java)

        Mockito.`when`(errorDataRepositoryMock.getError(Mockito.anyLong())).thenAnswer {
            getErrorResult
        }

        mErrorDataRepositoryMock = errorDataRepositoryMock
    }

    private fun initCallMock(
        requestFails: Boolean,
        errorBody: ResponseBody?,
        body: TestResponseBody?
    ) {
        val responseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(responseMock.body()).thenAnswer {
            mResponseBodyCallFlag = true

            body
        }
        Mockito.`when`(responseMock.errorBody()).thenAnswer {
            mResponseBodyErrorBodyCallFlag = true

            errorBody
        }

        val callMock = Mockito.mock(Call::class.java)

        Mockito.`when`(callMock.execute()).thenAnswer {
            mCallExecuteCallFlag = true

            if (requestFails) throw IllegalStateException()

            responseMock
        }

        mCallMock = callMock as Call<TestResponseBody>
    }

    @Test
    fun executeNetworkRequestWhenRequestFailsTest() {
        initExecuteNetworkRequestContext(requestFails = true)

        try {
            executeNetworkRequest(mErrorDataRepositoryMock, mCallMock)

            throw IllegalStateException()

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mCallExecuteCallFlag)
            Assert.assertEquals(DEFAULT_ERROR, e.error)
        }
    }

    @Test
    fun executeNetworkRequestWhenErrorResponseTest() {
        val errorBody = TestErrorResponseBody()

        initExecuteNetworkRequestContext(errorBody = errorBody)

        try {
            executeNetworkRequest(mErrorDataRepositoryMock, mCallMock)

            throw IllegalStateException()

        } catch (e: ErrorAppException) {
            Assert.assertTrue(mCallExecuteCallFlag)
            Assert.assertTrue(mResponseBodyErrorBodyCallFlag)
            Assert.assertEquals(DEFAULT_ERROR, e.error)
        }
    }

    @Test
    fun executeNetworkRequestWhenSuccessfulResponseTest() {
        val expectedResponseBody = TestResponseBody()

        initExecuteNetworkRequestContext(body = expectedResponseBody)

        val gottenResponseBody = executeNetworkRequest(mErrorDataRepositoryMock, mCallMock)

        Assert.assertTrue(mCallExecuteCallFlag)
        Assert.assertTrue(mResponseBodyErrorBodyCallFlag)
        Assert.assertTrue(mResponseBodyCallFlag)
        Assert.assertEquals(expectedResponseBody, gottenResponseBody)
    }
}