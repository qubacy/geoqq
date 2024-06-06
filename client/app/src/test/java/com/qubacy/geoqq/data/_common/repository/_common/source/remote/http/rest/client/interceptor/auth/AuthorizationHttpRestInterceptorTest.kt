package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common._test.util.mock.Base64MockUtil
import com.qubacy.geoqq._common.model.error.general.GeneralErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common._test.mock.AuthorizationRequestMiddlewareMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorResponseJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.impl.AuthorizationHttpRestInterceptorImpl
import okhttp3.HttpUrl
import okhttp3.Interceptor.Chain
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AuthorizationHttpRestInterceptorTest {
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mAuthorizationRequestMiddlewareMockContainer:
        AuthorizationRequestMiddlewareMockContainer

    private lateinit var mBufferedSourceMock: BufferedSource
    private lateinit var mChainMock: Chain

    private lateinit var mAuthorizationHttpRestInterceptor: AuthorizationHttpRestInterceptorImpl

    private var mResponseCodes: Array<Int> = arrayOf()
    private var mRequestUrlContainsAuthSegments: Boolean = false

    private var mCurResponseCodeIndex = 0

    private var mErrorJsonAdapterFromJson: ErrorResponse? = null

    private var mErrorJsonAdapterFromJsonCallFlag = false

    @Before
    fun setup() {
        Base64MockUtil.mockBase64()
        initAuthorizationHttpInterceptor()
    }

    @After
    fun clear() {
        mResponseCodes = arrayOf()
        mRequestUrlContainsAuthSegments = false

        mCurResponseCodeIndex = 0

        mErrorJsonAdapterFromJson = null

        mErrorJsonAdapterFromJsonCallFlag = false
    }

    private fun initAuthorizationHttpInterceptor() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mAuthorizationRequestMiddlewareMockContainer = AuthorizationRequestMiddlewareMockContainer()

        mChainMock = mockChain()

        val errorJsonAdapterMock = mockErrorJsonAdapter(mBufferedSourceMock)

        mAuthorizationHttpRestInterceptor = AuthorizationHttpRestInterceptorImpl(
            mErrorDataSourceMockContainer.errorDataSourceMock,
            errorJsonAdapterMock,
            mAuthorizationRequestMiddlewareMockContainer.authorizationRequestMiddleware
        )
    }

    private fun mockChain(): Chain {
        mBufferedSourceMock = Mockito.mock(BufferedSource::class.java)

        Mockito.`when`(mBufferedSourceMock.peek()).thenAnswer {
            mBufferedSourceMock
        }

        val responseBodyMock = Mockito.mock(ResponseBody::class.java)

        Mockito.`when`(responseBodyMock.source()).thenAnswer {
            mBufferedSourceMock
        }

        val requestBuilderMock = Mockito.mock(Request.Builder::class.java)

        Mockito.`when`(requestBuilderMock.addHeader(
            Mockito.anyString(), Mockito.anyString())
        ).thenAnswer {
            requestBuilderMock
        }
        Mockito.`when`(requestBuilderMock.build()).thenAnswer {
            Mockito.mock(Request::class.java)
        }

        val requestMock = Mockito.mock(Request::class.java)

        Mockito.`when`(requestMock.url()).thenAnswer {
            HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .addPathSegment(
                    if (mRequestUrlContainsAuthSegments)
                        AuthorizationHttpRestInterceptor.AUTH_URL_PATH_SEGMENTS.first()
                    else String()
                )
                .build()
        }
        Mockito.`when`(requestMock.newBuilder()).thenAnswer {
            requestBuilderMock
        }

        val chainMock = Mockito.mock(Chain::class.java)

        Mockito.`when`(chainMock.request()).thenAnswer {
            requestMock
        }
        Mockito.`when`(chainMock.proceed(AnyMockUtil.anyObject())).thenAnswer {
            val responseCode = mResponseCodes[mCurResponseCodeIndex]

            ++mCurResponseCodeIndex

            createResponse(responseCode, requestMock, responseBodyMock)
        }

        return chainMock
    }

    private fun createResponse(code: Int, request: Request, body: ResponseBody? = null): Response {
        return Response.Builder()
            .code(code)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(body)
            .message(String())
            .build()
    }

    private fun mockErrorJsonAdapter(errorBodySource: BufferedSource): ErrorResponseJsonAdapter {
        val errorJsonAdapterMock = Mockito.mock(ErrorResponseJsonAdapter::class.java)

        Mockito.`when`(errorJsonAdapterMock.fromJson(Mockito.eq(errorBodySource))).thenAnswer {
            mErrorJsonAdapterFromJsonCallFlag = true
            mErrorJsonAdapterFromJson
        }

        return errorJsonAdapterMock
    }

    @Test
    fun interceptAuthRequestTest() {
        mResponseCodes = arrayOf(200)
        mRequestUrlContainsAuthSegments = true

        mAuthorizationHttpRestInterceptor.intercept(mChainMock)

        Assert.assertFalse(mAuthorizationRequestMiddlewareMockContainer.processCallFlag)
    }

    @Test
    fun interceptErrorRequestTest() {
        mResponseCodes = arrayOf(400)
        mErrorJsonAdapterFromJson = ErrorResponse(ErrorResponseContent(1))

        mAuthorizationHttpRestInterceptor.intercept(mChainMock)

        Assert.assertTrue(mErrorJsonAdapterFromJsonCallFlag)
        Assert.assertTrue(mAuthorizationRequestMiddlewareMockContainer.processCallFlag)
    }

    @Test
    fun interceptAccessTokenErrorRequestTest() {
        mResponseCodes = arrayOf(400, 200)
        mErrorJsonAdapterFromJson = ErrorResponse(
            ErrorResponseContent(
            GeneralErrorType.INVALID_ACCESS_TOKEN.getErrorCode())
        )

        mAuthorizationHttpRestInterceptor.intercept(mChainMock)

        Assert.assertTrue(mErrorJsonAdapterFromJsonCallFlag)
        Assert.assertTrue(mAuthorizationRequestMiddlewareMockContainer.processCallFlag)
    }
}