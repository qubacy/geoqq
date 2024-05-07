package com.qubacy.geoqq.data._common.repository.source.remote.http.client.interceptor.auth

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common._test.util.mock.Base64MockUtil
import com.qubacy.geoqq._common.model.error.general.GeneralErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._test.mock.LocalTokenDataStoreDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.auth.AuthorizationHttpInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.response.UpdateTokensResponse
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

class AuthorizationHttpInterceptorTest {
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mLocalTokenDataStoreDataSourceMockContainer:
        LocalTokenDataStoreDataSourceMockContainer

    private lateinit var mBufferedSourceMock: BufferedSource
    private lateinit var mChainMock: Chain

    private lateinit var mAuthorizationHttpInterceptor: AuthorizationHttpInterceptor

    private var mResponseCode: Int? = null
    private var mRequestUrlContainsAuthSegments: Boolean = false

    private var mErrorJsonAdapterFromJson: ErrorResponse? = null

    private var mErrorJsonAdapterFromJsonCallFlag = false

    private var mUpdateTokensResponse: UpdateTokensResponse? = null

    private var mUpdateTokensCallFlag = false

    private var mOnUpdateTokens: (() -> Unit)? = null

    @Before
    fun setup() {
        Base64MockUtil.mockBase64()
        initAuthorizationHttpInterceptor()
    }

    @After
    fun clear() {
        mResponseCode = null
        mRequestUrlContainsAuthSegments = false

        mErrorJsonAdapterFromJson = null

        mErrorJsonAdapterFromJsonCallFlag = false

        mUpdateTokensResponse = null

        mUpdateTokensCallFlag = false

        mOnUpdateTokens = null
    }

    private fun initAuthorizationHttpInterceptor() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mLocalTokenDataStoreDataSourceMockContainer = LocalTokenDataStoreDataSourceMockContainer()

        mChainMock = mockChain()

        val errorJsonAdapterMock = mockErrorJsonAdapter(mBufferedSourceMock)
        val tokenHttpSourceMock = mockHttpTokenDataSource()

        mAuthorizationHttpInterceptor = AuthorizationHttpInterceptor(
            mErrorDataSourceMockContainer.errorDataSourceMock,
            errorJsonAdapterMock,
            mLocalTokenDataStoreDataSourceMockContainer.localTokenDataStoreDataSourceMock,
            tokenHttpSourceMock
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
                        AuthorizationHttpInterceptor.AUTH_URL_PATH_SEGMENTS.first()
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
            createResponse(mResponseCode!!, requestMock, responseBodyMock)
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

    private fun mockErrorJsonAdapter(errorBodySource: BufferedSource): ErrorJsonAdapter {
        val errorJsonAdapterMock = Mockito.mock(ErrorJsonAdapter::class.java)

        Mockito.`when`(errorJsonAdapterMock.fromJson(Mockito.eq(errorBodySource))).thenAnswer {
            mErrorJsonAdapterFromJsonCallFlag = true
            mErrorJsonAdapterFromJson
        }

        return errorJsonAdapterMock
    }

    private fun mockHttpTokenDataSource(): HttpTokenDataSource {
        val httpTokenDataSource = Mockito.mock(HttpTokenDataSource::class.java)

        Mockito.`when`(httpTokenDataSource.updateTokens(Mockito.anyString())).thenAnswer {
            mUpdateTokensCallFlag = true

            mOnUpdateTokens?.invoke()

            mUpdateTokensResponse
        }

        return httpTokenDataSource
    }

    @Test
    fun interceptAuthRequestTest() {
        mResponseCode = 200
        mRequestUrlContainsAuthSegments = true

        mAuthorizationHttpInterceptor.intercept(mChainMock)

        Assert.assertFalse(mLocalTokenDataStoreDataSourceMockContainer.getRefreshTokenCallFlag)
        Assert.assertFalse(mLocalTokenDataStoreDataSourceMockContainer.getAccessTokenCallFlag)
    }

    @Test
    fun interceptSuccessfulRequestTest() {
        mResponseCode = 200

        mLocalTokenDataStoreDataSourceMockContainer.getAccessToken =
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN
        mLocalTokenDataStoreDataSourceMockContainer.getRefreshToken =
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN

        mAuthorizationHttpInterceptor.intercept(mChainMock)

        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getAccessTokenCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getRefreshTokenCallFlag)
    }

    @Test
    fun interceptErrorRequestTest() {
        mResponseCode = 400
        mErrorJsonAdapterFromJson = ErrorResponse(ErrorResponseContent(1))

        mLocalTokenDataStoreDataSourceMockContainer.getAccessToken =
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN
        mLocalTokenDataStoreDataSourceMockContainer.getRefreshToken =
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN

        mAuthorizationHttpInterceptor.intercept(mChainMock)

        Assert.assertTrue(mErrorJsonAdapterFromJsonCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getAccessTokenCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getRefreshTokenCallFlag)
    }

    @Test
    fun interceptAccessTokenErrorRequestTest() {
        mResponseCode = 400
        mErrorJsonAdapterFromJson = ErrorResponse(ErrorResponseContent(
            GeneralErrorType.INVALID_ACCESS_TOKEN.getErrorCode()))

        mLocalTokenDataStoreDataSourceMockContainer.getRefreshToken =
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN
        mLocalTokenDataStoreDataSourceMockContainer.getAccessToken =
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN
        mUpdateTokensResponse = UpdateTokensResponse(
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN,
            LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN
        )
        mOnUpdateTokens = {
            Mockito.`when`(mChainMock.proceed(AnyMockUtil.anyObject())).thenAnswer {
                createResponse(200, mChainMock.request())
            }
        }

        mAuthorizationHttpInterceptor.intercept(mChainMock)

        Assert.assertTrue(mErrorJsonAdapterFromJsonCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getAccessTokenCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getRefreshTokenCallFlag)
        Assert.assertTrue(mUpdateTokensCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.saveTokensCallFlag)
    }
}