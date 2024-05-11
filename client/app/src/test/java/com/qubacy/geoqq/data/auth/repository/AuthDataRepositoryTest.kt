package com.qubacy.geoqq.data.auth.repository

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common._test.util.mock.Base64MockUtil.mockBase64
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.impl.RemoteTokenHttpRestDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.api.response.UpdateTokensResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest.impl.RemoteAuthHttpRestDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._test.mock.LocalTokenDataStoreDataSourceMockContainer
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignUpResponse
import com.qubacy.geoqq.data.auth.repository._common.source.local.database.impl.LocalAuthDatabaseDataSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AuthDataRepositoryTest : DataRepositoryTest<AuthDataRepositoryImpl>() {
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

        mDataRepository = AuthDataRepositoryImpl(
            mErrorDataRepositoryMockContainer.errorDataSourceMock,
            mLocalTokenDataStoreDataSourceMockContainer.localTokenDataStoreDataSourceMock,
            localAuthDatabaseDataSourceMock,
            httpTokenDataSourceMock,
            httpAuthDataSourceMock
        )
    }

    private fun mockLocalAuthDatabaseDataSource(): LocalAuthDatabaseDataSourceImpl {
        val localAuthDatabaseDataSourceMock = Mockito.mock(LocalAuthDatabaseDataSourceImpl::class.java)

        Mockito.`when`(localAuthDatabaseDataSourceMock.dropDataTables()).thenAnswer {
            mLocalDatabaseSourceDropDataTablesCallFlag = true

            Unit
        }

        return localAuthDatabaseDataSourceMock
    }

    private fun mockHttpTokenDataSource(): RemoteTokenHttpRestDataSourceImpl {
        val remoteTokenHttpRestDataSourceMock = Mockito.mock(RemoteTokenHttpRestDataSourceImpl::class.java)

        Mockito.`when`(remoteTokenHttpRestDataSourceMock.updateTokens(
            Mockito.anyString()
        )).thenAnswer {
            mHttpSourceUpdateTokensCallFlag = true
            mHttpSourceUpdateTokensResponse
        }

        return remoteTokenHttpRestDataSourceMock
    }

    private fun mockHttpAuthDataSource(): RemoteAuthHttpRestDataSourceImpl {
        val httpAuthDataSourceMock = Mockito.mock(RemoteAuthHttpRestDataSourceImpl::class.java)

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

    @Test
    fun signInWithTokenTest() = runTest {
        val refreshToken = VALID_TOKEN

        val updateTokensResponse = UpdateTokensResponse(
            "sign in accessToken",
            "sign in refreshToken"
        )

        mLocalTokenDataStoreDataSourceMockContainer.getRefreshToken = refreshToken
        mHttpSourceUpdateTokensResponse = updateTokensResponse

        mDataRepository.signIn()

        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getRefreshTokenCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.saveTokensCallFlag)
    }

    @Test
    fun signInWithLoginDataTest() = runTest {
        val signInResponse = SignInResponse(
            "sign in accessToken",
            "sign in refreshToken"
        )
        val login = "login"
        val password = "password"

        mHttpSourceSignInResponse = signInResponse

        mDataRepository.signIn(login, password)

        Assert.assertTrue(mHttpSourceSignInCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.saveTokensCallFlag)
    }

    @Test
    fun signUpTest() = runTest {
        val signUpResponse = SignUpResponse(
            "sign up accessToken",
            "sign up refreshToken"
        )
        val login = "login"
        val password = "password"

        mHttpSourceSignUpResponse = signUpResponse

        mDataRepository.signUp(login, password)

        Assert.assertTrue(mHttpSourceSignUpCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.saveTokensCallFlag)
    }

    @Test
    fun logoutTest() = runTest {
        mDataRepository.logout()

        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.clearTokensCallFlag)
        Assert.assertTrue(mLocalDatabaseSourceDropDataTablesCallFlag)
    }
}