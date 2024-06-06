package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common._test.mock.LocalTokenDataStoreDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common.AuthorizationRequestMiddleware
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.get.GetTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._test.mock.TokenDataRepositoryMockContainer
import okhttp3.Request
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AuthorizationRequestMiddlewareImplTest {
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mAuthorizationRequestMiddlewareImpl: AuthorizationRequestMiddlewareImpl

    private var mAddHeaderNameValue: Pair<String, String>? = null

    @Before
    fun setup() {
        initAuthorizationRequestMiddleware()
    }

    @After
    fun clear() {
        mAddHeaderNameValue = null
    }

    private fun initAuthorizationRequestMiddleware() {
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mAuthorizationRequestMiddlewareImpl = AuthorizationRequestMiddlewareImpl(
            mTokenDataRepositoryMockContainer.tokenDataRepository
        )
    }

    @Test
    fun processTest() {
        val request = mockRequest()
        val refreshToken = LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN
        val accessToken = LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN

        val expectedHeaderName = AuthorizationRequestMiddleware.AUTH_TOKEN_HEADER_NAME
        val expectedHeaderValue = AuthorizationRequestMiddleware
            .AUTH_TOKEN_HEADER_VALUE_FORMAT.format(accessToken)

        mTokenDataRepositoryMockContainer.getTokensDataResult =
            GetTokensDataResult(refreshToken, accessToken)

        mAuthorizationRequestMiddlewareImpl.process(request)

        val gottenHeaderName = mAddHeaderNameValue!!.first
        val gottenHeaderValue = mAddHeaderNameValue!!.second

        Assert.assertEquals(expectedHeaderName, gottenHeaderName)
        Assert.assertEquals(expectedHeaderValue, gottenHeaderValue)
    }

    private fun mockRequest(): Request {
        val requestBuilderMock = Mockito.mock(Request.Builder::class.java)

        Mockito.`when`(requestBuilderMock.addHeader(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            val name = it.arguments[0] as String
            val value = it.arguments[1] as String

            mAddHeaderNameValue = Pair(name, value)

            requestBuilderMock
        }
        Mockito.`when`(requestBuilderMock.build()).thenAnswer {
            Mockito.mock(Request::class.java)
        }

        val requestMock = Mockito.mock(Request::class.java)

        Mockito.`when`(requestMock.newBuilder()).thenAnswer {
            requestBuilderMock
        }

        return requestMock
    }
}