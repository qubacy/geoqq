package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.middleware.client.auth

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client.auth.AuthActionJsonMiddleware
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.get.GetTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.squareup.moshi.JsonWriter
import okio.Buffer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AuthActionJsonMiddlewareTest {
    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer

    private lateinit var mAuthActionJsonMiddleware: AuthActionJsonMiddleware

    @Before
    fun setup() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()

        mAuthActionJsonMiddleware = AuthActionJsonMiddleware(
            mErrorDataSourceMockContainer.errorDataSourceMock,
            mTokenDataRepositoryMockContainer.tokenDataRepository
        )
    }

    @After
    fun clear() {

    }

    @Test
    fun processTest() {
        val buffer = Buffer()
        val jsonWriter = JsonWriter.of(buffer).apply { beginObject() }
        val accessToken = "test"

        val expectedJson = "{\"${AuthActionJsonMiddleware.ACCESS_TOKEN_PROP_NAME}\":\"$accessToken\""

        mTokenDataRepositoryMockContainer.getTokensDataResult = GetTokensDataResult(
            accessToken = accessToken,
            refreshToken = String()
        )

        mAuthActionJsonMiddleware.process(jsonWriter)

        val gottenJson = buffer.readUtf8()

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertEquals(expectedJson, gottenJson)
    }
}