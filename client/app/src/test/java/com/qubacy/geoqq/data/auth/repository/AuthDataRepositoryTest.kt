package com.qubacy.geoqq.data.auth.repository

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common._test.util.mock.Base64MockUtil.mockBase64
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.response.UpdateTokensResponse
import com.qubacy.geoqq.data.auth.repository.source.http.HttpAuthDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._test.mock.LocalTokenDataStoreDataSourceMockContainer
import com.qubacy.geoqq.data.auth.repository.source.http.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository.source.http.api.response.SignUpResponse
import com.qubacy.geoqq.data.auth.repository.source.local.database.LocalAuthDatabaseDataSource
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AuthDataRepositoryTest : DataRepositoryTest<AuthDataRepository>() {
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

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataSourceMockContainer
    private lateinit var mLocalTokenDataStoreDataSourceMockContainer:
        LocalTokenDataStoreDataSourceMockContainer

    private var mLocalSourceLastAccessToken: String? = null
    private var mLocalSourceRefreshToken: String? = null

    private var mLocalSourceGetRefreshTokenCallFlag = false
    private var mLocalSourceSaveTokensAccessToken: String? = null
    private var mLocalSourceSaveTokensRefreshToken: String? = null
    private var mLocalSourceClearTokensCallFlag = false

    private var mLocalDatabaseSourceDropDataTablesCallFlag = false

    private var mHttpSourceUpdateTokensResponse: UpdateTokensResponse? = null

    private var mHttpSourceUpdateTokensCallFlag = false

    private var mHttpSourceSignInResponse: SignInResponse? = null
    private var mHttpSourceSignUpResponse: SignUpResponse? = null

    private var mHttpSourceSignInCallFlag = false
    private var mHttpSourceSignUpCallFlag = false

    @Before
    fun setup() {
        initTokenDataRepository()
    }

    @After
    fun clear() {
        mLocalSourceLastAccessToken = null
        mLocalSourceRefreshToken = null
        mLocalSourceSaveTokensAccessToken = null
        mLocalSourceSaveTokensRefreshToken = null

        mLocalSourceGetRefreshTokenCallFlag = false
        mLocalSourceClearTokensCallFlag = false

        mLocalDatabaseSourceDropDataTablesCallFlag = false

        mHttpSourceUpdateTokensResponse = null

        mHttpSourceUpdateTokensCallFlag = false

        mHttpSourceSignInResponse = null
        mHttpSourceSignUpResponse = null

        mHttpSourceSignInCallFlag = false
        mHttpSourceSignUpCallFlag = false
    }

    private fun initTokenDataRepository() = runTest {
        mErrorDataRepositoryMockContainer = ErrorDataSourceMockContainer()
        mLocalTokenDataStoreDataSourceMockContainer = LocalTokenDataStoreDataSourceMockContainer()

        val localAuthDatabaseDataSourceMock = mockLocalAuthDatabaseDataSource()

        val httpTokenDataSourceMock = mockHttpTokenDataSource()
        val httpAuthDataSourceMock = mockHttpAuthDataSource()

        mDataRepository = AuthDataRepository(
            mErrorDataRepositoryMockContainer.errorDataSourceMock,
            mLocalTokenDataStoreDataSourceMockContainer.localTokenDataStoreDataSourceMock,
            localAuthDatabaseDataSourceMock,
            httpTokenDataSourceMock,
            httpAuthDataSourceMock
        )
    }

    private fun mockLocalAuthDatabaseDataSource(): LocalAuthDatabaseDataSource {
        val localAuthDatabaseDataSourceMock = Mockito.mock(LocalAuthDatabaseDataSource::class.java)

        Mockito.`when`(localAuthDatabaseDataSourceMock.dropDataTables()).thenAnswer {
            mLocalDatabaseSourceDropDataTablesCallFlag = true

            Unit
        }

        return localAuthDatabaseDataSourceMock
    }

    private fun mockHttpTokenDataSource(): HttpTokenDataSource {
        val httpTokenDataSourceMock = Mockito.mock(HttpTokenDataSource::class.java)

        Mockito.`when`(httpTokenDataSourceMock.updateTokens(
            Mockito.anyString()
        )).thenAnswer {
            mHttpSourceUpdateTokensCallFlag = true
            mHttpSourceUpdateTokensResponse
        }

        return httpTokenDataSourceMock
    }

    private fun mockHttpAuthDataSource(): HttpAuthDataSource {
        val httpAuthDataSourceMock = Mockito.mock(HttpAuthDataSource::class.java)

        Mockito.`when`(httpAuthDataSourceMock.signIn(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceSignInCallFlag = true
            mHttpSourceSignInResponse
        }

        Mockito.`when`(httpAuthDataSourceMock.signUp(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceSignUpCallFlag = true
            mHttpSourceSignUpResponse
        }

        return httpAuthDataSourceMock
    }

//    @Test
//    fun getTokensWhenLocalTokensInitializedTest() = runTest {
//        val expectedAccessToken = VALID_TOKEN
//        val expectedRefreshToken = VALID_TOKEN
//
//        mLocalSourceLastAccessToken = expectedAccessToken
//        mLocalSourceRefreshToken = expectedRefreshToken
//
//        val gottenGetTokensResult = mDataRepository.getTokens()
//
//        Assert.assertTrue(mLocalSourceGetRefreshTokenCallFlag)
//        Assert.assertEquals(expectedAccessToken, gottenGetTokensResult.accessToken)
//        Assert.assertEquals(expectedRefreshToken, gottenGetTokensResult.refreshToken)
//    }

//    @Test
//    fun getTokensWhenAccessTokenNotInitializedTest() = runTest {
//        val expectedUpdateTokensResponse = UpdateTokensResponse(
//            "updated accessToken",
//            "updated refreshToken"
//        )
//
//        mLocalSourceLastAccessToken = null
//        mLocalSourceRefreshToken = VALID_TOKEN
//        mHttpCallExecutorMockContainer.response = expectedUpdateTokensResponse
//
//        val gottenGetTokensResult = mDataRepository.getTokens()
//
//        Assert.assertTrue(mLocalSourceGetRefreshTokenCallFlag)
//        Assert.assertTrue(mHttpSourceUpdateTokensCallFlag)
//        Assert.assertTrue(mHttpCallExecutorMockContainer.executeNetworkRequestCallFlag)
//        Assert.assertEquals(expectedUpdateTokensResponse.accessToken, mLocalSourceSaveTokensAccessToken)
//        Assert.assertEquals(expectedUpdateTokensResponse.refreshToken, mLocalSourceSaveTokensRefreshToken)
//        Assert.assertEquals(expectedUpdateTokensResponse.accessToken, gottenGetTokensResult.accessToken)
//        Assert.assertEquals(expectedUpdateTokensResponse.refreshToken, gottenGetTokensResult.refreshToken)
//    }

//    @Test
//    fun getTokensWhenRefreshTokenInvalidTest() = runTest {
//        val expectedError = TestError.normal
//
//        mLocalSourceLastAccessToken = null
//        mLocalSourceRefreshToken = null
//        mErrorDataRepositoryMockContainer.getError = expectedError
//
//        try {
//            mDataRepository.getTokens()
//
//        } catch (e: ErrorAppException) {
//            Assert.assertEquals(expectedError, e.error)
//        }
//    }

    @Test
    fun signInWithTokenTest() = runTest {
        val refreshToken = VALID_TOKEN

        val expectedUpdateTokensResponse = UpdateTokensResponse(
            "sign in accessToken",
            "sign in refreshToken"
        )

        mLocalSourceRefreshToken = refreshToken
        mHttpSourceUpdateTokensResponse = expectedUpdateTokensResponse

        mDataRepository.signIn()

        Assert.assertTrue(mLocalSourceGetRefreshTokenCallFlag)
        Assert.assertEquals(expectedUpdateTokensResponse.accessToken, mLocalSourceSaveTokensAccessToken)
        Assert.assertEquals(expectedUpdateTokensResponse.refreshToken, mLocalSourceSaveTokensRefreshToken)
    }

    @Test
    fun signInWithLoginDataTest() = runTest {
        val expectedTokenSignInResponse = SignInResponse(
            "sign in accessToken",
            "sign in refreshToken"
        )
        val login = "login"
        val password = "password"

        mHttpSourceSignInResponse = expectedTokenSignInResponse

        mDataRepository.signIn(login, password)

        Assert.assertTrue(mHttpSourceSignInCallFlag)
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

        mDataRepository.signUp(login, password)

        Assert.assertTrue(mHttpSourceSignUpCallFlag)
        Assert.assertEquals(expectedTokenSignUpResponse.accessToken, mLocalSourceSaveTokensAccessToken)
        Assert.assertEquals(expectedTokenSignUpResponse.refreshToken, mLocalSourceSaveTokensRefreshToken)
    }

    @Test
    fun logoutTest() = runTest {
        mDataRepository.logout()

        Assert.assertTrue(mLocalSourceClearTokensCallFlag)
        Assert.assertTrue(mLocalDatabaseSourceDropDataTablesCallFlag)
    }

//    @Test
//    fun checkTokenForValidityTest() {
//        val expectedIsTokenValid = true
//        val gottenIsTokenValid = mDataRepository.checkTokenForValidity(VALID_TOKEN)
//
//        Assert.assertEquals(expectedIsTokenValid, gottenIsTokenValid)
//    }

//    @Test
//    fun getTokenPayloadTest() {
//        val token = VALID_TOKEN
//        val expectedTokenPayload = VALID_TOKEN_PAYLOAD
//
//        val gottenTokenPayload = mDataRepository.getTokenPayload(token)!!
//
//        assertTokenPayload(expectedTokenPayload, gottenTokenPayload)
//    }

//    @Test
//    fun getAccessTokenPayloadTest() = runTest {
//        val expectedTokenPayload = VALID_TOKEN_PAYLOAD
//
//        mLocalSourceLastAccessToken = VALID_TOKEN
//
//        val gottenTokenPayload = mDataRepository.getAccessTokenPayload()
//
//        assertTokenPayload(expectedTokenPayload, gottenTokenPayload)
//    }
//
//    private fun assertTokenPayload(
//        expectedTokenPayload: Map<String, Claim>,
//        gottenTokenPayload: Map<String, Claim>
//    ) {
//        Assert.assertEquals(expectedTokenPayload.size, gottenTokenPayload.size)
//
//        for (expectedTokenEntry in expectedTokenPayload) {
//            val gottenTokenClaim = gottenTokenPayload[expectedTokenEntry.key]!!
//
//            Assert.assertEquals(expectedTokenEntry.value.asLong(), gottenTokenClaim.asLong())
//        }
//    }
}