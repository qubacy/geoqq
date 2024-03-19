package com.qubacy.geoqq.data.token.repository

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common._test._common.util.mock.Base64MockUtil.mockBase64
import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.http.response.SignInResponse
import com.qubacy.geoqq.data.token.repository.source.http.response.SignUpResponse
import com.qubacy.geoqq.data.token.repository.source.http.response.UpdateTokensResponse
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.util.extension.signIn
import com.qubacy.geoqq.data.token.repository.util.extension.signUp
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response

class TokenDataRepositoryTest {
    companion object {
        const val VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MzUxNjIzOTAyMn0." +
                "hvYjFTg5JYV1AIoP1cMLWSScRrFhr7lFwYow4eVQGTc"
        val VALID_TOKEN_PAYLOAD: Map<String, Claim>

        init {
            mockBase64()

            VALID_TOKEN_PAYLOAD = JWT(VALID_TOKEN).claims
        }
    }

    private lateinit var mTokenDataRepository: TokenDataRepository

    private var mErrorDataRepositoryGetError: Error? = null

    private var mLocalSourceLastAccessToken: String? = null
    private var mLocalSourceRefreshToken: String? = null

    private var mLocalSourceGetRefreshTokenCallFlag = false
    private var mLocalSourceSaveTokensAccessToken: String? = null
    private var mLocalSourceSaveTokensRefreshToken: String? = null
    private var mLocalSourceClearTokensCallFlag = false

    private var mHttpSourceUpdateTokensResponse: UpdateTokensResponse? = null
    private var mHttpSourceSignInResponse: SignInResponse? = null
    private var mHttpSourceSignUpResponse: SignUpResponse? = null

    private var mHttpSourceUpdateTokensCallFlag = false
    private var mHttpSourceSignInCallFlag = false
    private var mHttpSourceSignUpCallFlag = false

    @Before
    fun setup() {
        initTokenDataRepository()
    }

    @After
    fun clear() {
        mErrorDataRepositoryGetError = null
        mLocalSourceLastAccessToken = null
        mLocalSourceRefreshToken = null
        mLocalSourceGetRefreshTokenCallFlag = false
        mLocalSourceSaveTokensAccessToken = null
        mLocalSourceSaveTokensRefreshToken = null
        mLocalSourceClearTokensCallFlag = false
        mHttpSourceUpdateTokensResponse = null
        mHttpSourceSignInResponse = null
        mHttpSourceSignUpResponse = null
        mHttpSourceUpdateTokensCallFlag = false
        mHttpSourceSignInCallFlag = false
        mHttpSourceSignUpCallFlag = false
    }

    private fun initTokenDataRepository() = runTest {
        val errorDataRepositoryMock = Mockito.mock(ErrorDataRepository::class.java)

        Mockito.`when`(errorDataRepositoryMock.getError(Mockito.anyLong())).thenAnswer {
            mErrorDataRepositoryGetError
        }

        val localTokenDataSourceMock = Mockito.mock(LocalTokenDataSource::class.java)

        Mockito.`when`(localTokenDataSourceMock.lastAccessToken).thenAnswer {
            mLocalSourceLastAccessToken
        }
        Mockito.`when`(localTokenDataSourceMock.getRefreshToken()).thenAnswer {
            mLocalSourceGetRefreshTokenCallFlag = true

            mLocalSourceRefreshToken
        }
        Mockito.`when`(localTokenDataSourceMock.saveTokens(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            val accessToken = it.arguments[0] as String
            val refreshToken = it.arguments[1] as String

            mLocalSourceSaveTokensAccessToken = accessToken
            mLocalSourceSaveTokensRefreshToken = refreshToken

            Unit
        }
        Mockito.`when`(localTokenDataSourceMock.clearTokens()).thenAnswer {
            mLocalSourceClearTokensCallFlag = true

            Unit
        }

        val httpTokenDataSourceMock = Mockito.mock(HttpTokenDataSource::class.java)

        val updateTokensResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(updateTokensResponseMock.body()).thenAnswer {
            mHttpSourceUpdateTokensResponse
        }

        val updateTokensRequestMock = Mockito.mock(Call::class.java)

        Mockito.`when`(updateTokensRequestMock.execute()).thenAnswer {
            updateTokensResponseMock
        }

        Mockito.`when`(httpTokenDataSourceMock.updateTokens(Mockito.anyString())).thenAnswer {
            mHttpSourceUpdateTokensCallFlag = true

            updateTokensRequestMock
        }

        val signInResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(signInResponseMock.body()).thenAnswer {
            mHttpSourceSignInResponse
        }

        val signInRequestMock = Mockito.mock(Call::class.java)

        Mockito.`when`(signInRequestMock.execute()).thenAnswer {
            signInResponseMock
        }

        Mockito.`when`(httpTokenDataSourceMock.signIn(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceSignInCallFlag = true

            signInRequestMock
        }

        val signUpResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(signUpResponseMock.body()).thenAnswer {
            mHttpSourceSignUpResponse
        }

        val signUpRequestMock = Mockito.mock(Call::class.java)

        Mockito.`when`(signUpRequestMock.execute()).thenAnswer {
            signUpResponseMock
        }

        Mockito.`when`(httpTokenDataSourceMock.signUp(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceSignUpCallFlag = true

            signUpRequestMock
        }

        mTokenDataRepository = TokenDataRepository(
            errorDataRepositoryMock,
            localTokenDataSourceMock,
            httpTokenDataSourceMock
        )
    }

    @Test
    fun getTokensWhenLocalTokensInitializedTest() = runTest {
        val expectedAccessToken = VALID_TOKEN
        val expectedRefreshToken = VALID_TOKEN

        mLocalSourceLastAccessToken = expectedAccessToken
        mLocalSourceRefreshToken = expectedRefreshToken

        val gottenGetTokensResult = mTokenDataRepository.getTokens()

        Assert.assertTrue(mLocalSourceGetRefreshTokenCallFlag)
        Assert.assertEquals(expectedAccessToken, gottenGetTokensResult.accessToken)
        Assert.assertEquals(expectedRefreshToken, gottenGetTokensResult.refreshToken)
    }

    @Test
    fun getTokensWhenAccessTokenNotInitializedTest() = runTest {
        val expectedUpdateTokensResponse = UpdateTokensResponse(
            "updated accessToken",
            "updated refreshToken"
        )

        mLocalSourceLastAccessToken = null
        mLocalSourceRefreshToken = VALID_TOKEN
        mHttpSourceUpdateTokensResponse = expectedUpdateTokensResponse

        val gottenGetTokensResult = mTokenDataRepository.getTokens()

        Assert.assertTrue(mLocalSourceGetRefreshTokenCallFlag)
        Assert.assertTrue(mHttpSourceUpdateTokensCallFlag)
        Assert.assertEquals(expectedUpdateTokensResponse.accessToken, mLocalSourceSaveTokensAccessToken)
        Assert.assertEquals(expectedUpdateTokensResponse.refreshToken, mLocalSourceSaveTokensRefreshToken)
        Assert.assertEquals(expectedUpdateTokensResponse.accessToken, gottenGetTokensResult.accessToken)
        Assert.assertEquals(expectedUpdateTokensResponse.refreshToken, gottenGetTokensResult.refreshToken)
    }

    @Test
    fun getTokensWhenRefreshTokenInvalidTest() = runTest {
        val expectedError = TestError.normal

        mLocalSourceLastAccessToken = null
        mLocalSourceRefreshToken = null
        mErrorDataRepositoryGetError = expectedError

        try {
            mTokenDataRepository.getTokens()

        } catch (e: ErrorAppException) {
            Assert.assertEquals(expectedError, e.error)
        }
    }

    @Test
    fun signInTest() = runTest {
        val expectedTokenSignInResponse = SignInResponse(
            "sign in accessToken",
            "sign in refreshToken"
        )
        val login = "login"
        val password = "password"

        mHttpSourceSignInResponse = expectedTokenSignInResponse

        mTokenDataRepository.signIn(login, password)

        Assert.assertEquals(expectedTokenSignInResponse.accessToken, mLocalSourceSaveTokensAccessToken)
        Assert.assertEquals(expectedTokenSignInResponse.refreshToken, mLocalSourceSaveTokensRefreshToken)
    }

    @Test
    fun signUpTest() = runTest {
        val expectedTokenSignUpResponse = SignUpResponse(
            "sign up accessToken",
            "sign up refreshToken"
        )
        val login = "login"
        val password = "password"

        mHttpSourceSignUpResponse = expectedTokenSignUpResponse

        mTokenDataRepository.signUp(login, password)

        Assert.assertEquals(expectedTokenSignUpResponse.accessToken, mLocalSourceSaveTokensAccessToken)
        Assert.assertEquals(expectedTokenSignUpResponse.refreshToken, mLocalSourceSaveTokensRefreshToken)
    }

    @Test
    fun clearTokensTest() = runTest {
        mTokenDataRepository.clearTokens()

        Assert.assertTrue(mLocalSourceClearTokensCallFlag)
    }

    @Test
    fun checkTokenForValidityTest() {
        val expectedIsTokenValid = true
        val gottenIsTokenValid = mTokenDataRepository.checkTokenForValidity(VALID_TOKEN)

        Assert.assertEquals(expectedIsTokenValid, gottenIsTokenValid)
    }

    @Test
    fun getTokenPayloadTest() {
        val token = VALID_TOKEN
        val expectedTokenPayload = VALID_TOKEN_PAYLOAD

        val gottenTokenPayload = mTokenDataRepository.getTokenPayload(token)!!

        assertTokenPayload(expectedTokenPayload, gottenTokenPayload)
    }

    @Test
    fun getAccessTokenPayloadTest() {
        val expectedTokenPayload = VALID_TOKEN_PAYLOAD

        mLocalSourceLastAccessToken = VALID_TOKEN

        val gottenTokenPayload = mTokenDataRepository.getAccessTokenPayload()

        assertTokenPayload(expectedTokenPayload, gottenTokenPayload)
    }

    private fun assertTokenPayload(
        expectedTokenPayload: Map<String, Claim>,
        gottenTokenPayload: Map<String, Claim>
    ) {
        Assert.assertEquals(expectedTokenPayload.size, gottenTokenPayload.size)

        for (expectedTokenEntry in expectedTokenPayload) {
            val gottenTokenClaim = gottenTokenPayload[expectedTokenEntry.key]!!

            Assert.assertEquals(expectedTokenEntry.value.asLong(), gottenTokenClaim.asLong())
        }
    }
}